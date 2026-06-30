package com.khaled.shopsphere.payment.impl;

import com.khaled.shopsphere.config.StripeProperties;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderStatus;
import com.khaled.shopsphere.payment.Payment;
import com.khaled.shopsphere.payment.PaymentRepository;
import com.khaled.shopsphere.payment.PaymentStatus;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl Unit Tests")
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StripeProperties stripeProperties;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    UUID orderId;
    Order order;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        order = Order.builder()
                .id(orderId)
                .totalPrice(BigDecimal.valueOf(100))
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
    }

    @Nested
    @DisplayName("Create Stripe Session Tests")
    class CreateStripeSession {

        @Test
        @DisplayName("Should create stripe session successfully")
        void shouldCreateStripeSessionSuccessfully() {
            // Given
            Session session = mock(Session.class);

            when(session.getId()).thenReturn("sess_123");
            when(session.getUrl()).thenReturn("https://checkout.stripe.com/test");

            Payment savedPayment = Payment.builder()
                    .id(UUID.randomUUID())
                    .order(order)
                    .amount(order.getTotalPrice())
                    .stripeSessionId("sess_123")
                    .status(PaymentStatus.PENDING)
                    .build();

            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
            when(stripeProperties.getSuccessUrl()).thenReturn("http://localhost/success");
            when(stripeProperties.getCancelUrl()).thenReturn("http://localhost/cancel");
            when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

            try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {

                mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(session);

                // When
                PaymentSessionResult result = paymentService.createStripeSession(order);

                // Then
                assertEquals("sess_123", result.getSessionId());
                assertEquals("https://checkout.stripe.com/test", result.getSessionUrl());
                assertEquals(savedPayment, result.getPayment());

                verify(paymentRepository).save(any(Payment.class));
            }
        }

        @Test
        @DisplayName("Should throw exception when payment already exists")
        void shouldThrowExceptionWhenPaymentAlreadyExists() {
            // Given
            Payment payment = Payment.builder().build();

            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> paymentService.createStripeSession(order)
            );

            assertEquals(PAYMENT_ALREADY_EXISTS.getDefaultMessage(), exception.getMessage());

            verify(paymentRepository).findByOrderId(orderId);
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when stripe session creation fails")
        void shouldThrowExceptionWhenStripeSessionCreationFails() {
            // Given
            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
            when(stripeProperties.getSuccessUrl()).thenReturn("http://localhost/success");
            when(stripeProperties.getCancelUrl()).thenReturn("http://localhost/cancel");

            try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {

                mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                        .thenThrow(mock(StripeException.class));

                // When & Then
                BusinessException exception = assertThrows(
                        BusinessException.class,
                        () -> paymentService.createStripeSession(order)
                );

                assertEquals(
                        STRIPE_SESSION_CREATION_FAILED.getDefaultMessage(),
                        exception.getMessage()
                );

                verify(paymentRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("Handle Checkout Completed Tests")
    class HandleCheckoutCompleted {

        @Test
        @DisplayName("Should mark payment and order as paid")
        void shouldMarkPaymentAndOrderAsPaid() {
            // Given
            String sessionId = "session_123";
            String paymentIntentId = "pi_123";

            Payment payment = Payment.builder()
                    .order(order)
                    .status(PaymentStatus.PENDING)
                    .build();

            when(paymentRepository.findByStripeSessionId(sessionId)).thenReturn(Optional.of(payment));

            // When
            paymentService.handleCheckoutCompleted(sessionId, paymentIntentId);

            // Then
            assertEquals(paymentIntentId, payment.getStripePaymentIntentId());
            assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());
            assertNotNull(payment.getPaidAt());
            assertEquals(OrderStatus.PAID, order.getStatus());
            verify(paymentRepository).findByStripeSessionId(sessionId);
        }

        @Test
        @DisplayName("Should do nothing when payment is already succeeded")
        void shouldDoNothingWhenPaymentIsAlreadySucceeded() {
            // Given
            String sessionId = "session_123";
            String paymentIntentId = "pi_123";

            Payment payment = Payment.builder()
                    .status(PaymentStatus.SUCCEEDED)
                    .build();

            when(paymentRepository.findByStripeSessionId(sessionId)).thenReturn(Optional.of(payment));

            // When
            paymentService.handleCheckoutCompleted(
                    sessionId,
                    paymentIntentId
            );

            // Then
            assertEquals(PaymentStatus.SUCCEEDED, payment.getStatus());

            verify(paymentRepository).findByStripeSessionId(sessionId);
        }

        @Test
        @DisplayName("Should throw exception when payment is not found")
        void shouldThrowExceptionWhenPaymentIsNotFound() {
            // Given
            String sessionId = "session_123";
            String paymentIntentId = "pi_123";

            when(paymentRepository.findByStripeSessionId(sessionId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> paymentService.handleCheckoutCompleted(
                            sessionId,
                            paymentIntentId
                    )
            );

            assertEquals(PAYMENT_NOT_FOUND.getDefaultMessage(), exception.getMessage());

            verify(paymentRepository).findByStripeSessionId(sessionId);
        }

    }

    @Nested
    @DisplayName("Mark Failed Tests")
    class MarkFailed {

        @Test
        @DisplayName("Should mark payment as failed")
        void shouldMarkPaymentAsFailed() {
            // Given
            String paymentIntentId = "pi_123";

            Payment payment = Payment.builder()
                    .status(PaymentStatus.PENDING)
                    .order(order)
                    .build();

            when(paymentRepository.findByStripePaymentIntentId(paymentIntentId))
                    .thenReturn(Optional.of(payment));

            // When
            paymentService.markFailed(paymentIntentId);

            // Then
            assertEquals(PaymentStatus.FAILED, payment.getStatus());

            verify(paymentRepository).findByStripePaymentIntentId(paymentIntentId);
        }

        @Test
        @DisplayName("Should throw exception when payment is not found")
        void shouldThrowExceptionWhenPaymentIsNotFound() {
            // Given
            String paymentIntentId = "pi_123";

            when(paymentRepository.findByStripePaymentIntentId(paymentIntentId))
                    .thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> paymentService.markFailed(paymentIntentId)
            );

            assertEquals(PAYMENT_NOT_FOUND.getDefaultMessage(), exception.getMessage());

            verify(paymentRepository)
                    .findByStripePaymentIntentId(paymentIntentId);
        }

        @Test
        @DisplayName("Should do nothing when payment is already failed")
        void shouldDoNothingWhenPaymentIsAlreadyFailed() {
            // Given
            String paymentIntentId = "pi_123";

            Payment payment = Payment.builder()
                    .status(PaymentStatus.FAILED)
                    .build();

            when(paymentRepository.findByStripePaymentIntentId(paymentIntentId))
                    .thenReturn(Optional.of(payment));

            // When
            paymentService.markFailed(paymentIntentId);

            // Then
            assertEquals(
                    PaymentStatus.FAILED,
                    payment.getStatus()
            );

            verify(paymentRepository)
                    .findByStripePaymentIntentId(paymentIntentId);
        }
    }
}