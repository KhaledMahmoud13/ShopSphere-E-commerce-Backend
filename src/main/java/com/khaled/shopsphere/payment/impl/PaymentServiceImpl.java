package com.khaled.shopsphere.payment.impl;

import com.khaled.shopsphere.config.StripeProperties;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderRepository;
import com.khaled.shopsphere.order.OrderStatus;
import com.khaled.shopsphere.payment.*;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StripeProperties stripeProperties;

    @Override
    @Transactional
    public PaymentSessionResult createStripeSession(Order order) {
        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BusinessException(PAYMENT_ALREADY_EXISTS);
        }

        long amountInCents = order.getTotalPrice()
                .movePointRight(2)
                .longValue();

        Session session;
        try {
            session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(stripeProperties.getSuccessUrl())
                            .setCancelUrl(stripeProperties.getCancelUrl())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("USD")
                                                            .setUnitAmount(amountInCents)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("ShopSphere Purchase")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            ).putMetadata("orderId", order.getId().toString())
                            .build()
            );
        } catch (StripeException e) {
            log.error("Failed to create Stripe session for order {}: {}", order.getId(), e.getMessage());
            throw new BusinessException(STRIPE_SESSION_CREATION_FAILED);
        }

        Payment payment = paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .amount(order.getTotalPrice())
                        .stripeSessionId(session.getId())
                        .status(PaymentStatus.PENDING)
                        .build()
        );

        return PaymentSessionResult.builder()
                .payment(payment)
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    @Override
    @Transactional
    public void markSucceeded(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            return;
        }

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaidAt(Instant.now());
        payment.getOrder().setStatus(OrderStatus.PAID);

        log.info("Payment {} marked SUCCEEDED for order {}", payment.getId(), payment.getOrder().getId());
    }

    @Override
    @Transactional
    public void markFailed(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);

        log.info("Payment {} marked FAILED for order {}", payment.getId(), payment.getOrder().getId());
    }

    @Override
    @Transactional
    public void attachPaymentIntent(String stripeSessionId, String paymentIntentId) {
        Payment payment = paymentRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new BusinessException(PAYMENT_NOT_FOUND));
        payment.setStripePaymentIntentId(paymentIntentId);
    }

    @Override
    @Transactional
    public PaymentSessionResult getOrCreateSession(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);

        if (existing.isPresent()) {
            Payment payment = existing.get();

            switch (payment.getStatus()) {
                case SUCCEEDED -> throw new BusinessException(PAYMENT_ALREADY_SUCCEEDED);
                case PENDING -> {
                    try {
                        Session session = Session.retrieve(payment.getStripeSessionId());
                        if ("open".equals(session.getStatus())) {
                            return PaymentSessionResult.builder()
                                    .payment(payment)
                                    .sessionId(session.getId())
                                    .sessionUrl(session.getUrl())
                                    .build();
                        }
                    } catch (StripeException e) {
                        log.warn("Could not retrieve Stripe session {}: {}", payment.getStripeSessionId(), e.getMessage());
                    }

                    return refreshSession(payment, order);
                }
                case FAILED -> {
                    return refreshSession(payment, order);
                }
            }
        }

        return createStripeSession(order);
    }

    private PaymentSessionResult refreshSession(Payment payment, Order order) {
        long amountInCents = order.getTotalPrice().movePointRight(2).longValue();

        Session session;
        try {
            session = Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(stripeProperties.getSuccessUrl())
                            .setCancelUrl(stripeProperties.getCancelUrl())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("usd")
                                                            .setUnitAmount(amountInCents)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("ShopSphere Order #" + order.getId())
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .putMetadata("orderId", order.getId().toString())
                            .build()
            );
        } catch (StripeException e) {
            log.error("Failed to refresh Stripe session for order {}: {}", order.getId(), e.getMessage());
            throw new BusinessException(STRIPE_SESSION_CREATION_FAILED);
        }

        payment.setStripeSessionId(session.getId());
        payment.setStripePaymentIntentId(null);
        payment.setStatus(PaymentStatus.PENDING);

        return PaymentSessionResult.builder()
                .payment(payment)
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
