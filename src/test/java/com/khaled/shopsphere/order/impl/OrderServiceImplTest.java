package com.khaled.shopsphere.order.impl;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.inventory.InventoryService;
import com.khaled.shopsphere.order.*;
import com.khaled.shopsphere.order.event.OrderCreatedEvent;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Unit Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private OrderMapper mapper;
    @Mock
    private OrderStatusValidator orderStatusValidator;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .emailVerified(true)
                .build();
    }

    @Nested
    @DisplayName("Create From Cart Tests")
    class CreateFromCartTests {

        @Test
        @DisplayName("Should create order from cart successfully")
        void shouldCreateOrderFromCartSuccessfully() {
            // Given
            UUID productId = UUID.randomUUID();
            Product product = Product.builder()
                    .id(productId)
                    .name("Laptop")
                    .price(BigDecimal.valueOf(1000))
                    .build();
            OrderItemRequest request = new OrderItemRequest(productId, 2);
            Map<UUID, Product> productMap = Map.of(productId, product);
            when(inventoryService.validateAndDeduct(anyList())).thenReturn(productMap);
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            Order result = orderService.createFromCart(List.of(request), testUser);

            // Then
            assertEquals(testUser, result.getUser());
            assertEquals(OrderStatus.PENDING_PAYMENT, result.getStatus());
            assertEquals(BigDecimal.valueOf(2000), result.getTotalPrice());

            assertEquals(1, result.getItems().size());

            OrderItem item = result.getItems().getFirst();

            assertEquals(productId, item.getProductId());
            assertEquals("Laptop", item.getProductName());
            assertEquals(2, item.getQuantity());
            assertEquals(BigDecimal.valueOf(1000), item.getPriceAtPurchase());

            verify(inventoryService).validateAndDeduct(anyList());
            verify(orderRepository).save(any(Order.class));
            verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));

        }

        @Test
        @DisplayName("Should throw exception when order items are empty")
        void shouldThrowExceptionWhenOrderItemsAreEmpty() {
            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.createFromCart(
                            List.of(),
                            testUser
                    )
            );

            assertEquals(ORDER_HAS_NO_ITEMS.getDefaultMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when order items are null")
        void shouldThrowExceptionWhenOrderItemsAreNull() {
            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.createFromCart(
                            null,
                            testUser
                    )
            );

            assertEquals(ORDER_HAS_NO_ITEMS.getDefaultMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when product is not found")
        void shouldThrowExceptionWhenProductIsNotFound() {
            // Given
            UUID productId = UUID.randomUUID();

            OrderItemRequest request = new OrderItemRequest(productId, 1);

            when(inventoryService.validateAndDeduct(anyList())).thenReturn(Map.of());

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.createFromCart(
                            List.of(request),
                            testUser
                    )
            );

            assertEquals(PRODUCT_NOT_FOUND.getDefaultMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when quantity is zero")
        void shouldThrowExceptionWhenQuantityIsZero() {
            // Given
            UUID productId = UUID.randomUUID();

            Product product = Product.builder()
                    .id(productId)
                    .name("Laptop")
                    .price(BigDecimal.valueOf(1000))
                    .build();

            OrderItemRequest request = new OrderItemRequest(productId, 0);

            when(inventoryService.validateAndDeduct(anyList()))
                    .thenReturn(Map.of(productId, product));

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.createFromCart(
                            List.of(request),
                            testUser
                    )
            );

            assertEquals(INVALID_ITEM_QUANTITY.getDefaultMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // Given
            UUID productId = UUID.randomUUID();

            Product product = Product.builder()
                    .id(productId)
                    .name("Laptop")
                    .price(BigDecimal.valueOf(1000))
                    .build();

            OrderItemRequest request = new OrderItemRequest(productId, -1);

            when(inventoryService.validateAndDeduct(anyList()))
                    .thenReturn(Map.of(productId, product));

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.createFromCart(
                            List.of(request),
                            testUser
                    )
            );

            assertEquals(INVALID_ITEM_QUANTITY.getDefaultMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel pending payment order successfully")
        void shouldCancelPendingPaymentOrderSuccessfully() {
            // Given
            UUID orderId = UUID.randomUUID();

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING_PAYMENT)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When
            orderService.cancelOrder(orderId);

            // Then
            assertEquals(OrderStatus.CANCELLED, order.getStatus());

            verify(orderRepository).findById(orderId);
            verify(inventoryService).restoreStock(order);
        }

        @Test
        @DisplayName("Should throw exception when order is not found")
        void shouldThrowExceptionWhenOrderIsNotFound() {
            // Given
            UUID orderId = UUID.randomUUID();

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.cancelOrder(orderId)
            );

            assertEquals(ORDER_NOT_FOUND.getDefaultMessage(), exception.getMessage());

            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("Should throw exception when order is already cancelled")
        void shouldThrowExceptionWhenOrderIsAlreadyCancelled() {
            // Given
            UUID orderId = UUID.randomUUID();

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.CANCELLED)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.cancelOrder(orderId)
            );

            assertEquals(ORDER_ALREADY_CANCELLED.getDefaultMessage(), exception.getMessage());

            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("Should throw exception when order cannot be cancelled")
        void shouldThrowExceptionWhenOrderCannotBeCancelled() {
            // Given
            UUID orderId = UUID.randomUUID();

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.SHIPPED)
                    .build();

            when(orderRepository.findById(orderId))
                    .thenReturn(Optional.of(order));

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.cancelOrder(orderId)
            );

            assertEquals(
                    String.format(ORDER_CANNOT_BE_CANCELLED.getDefaultMessage(), OrderStatus.SHIPPED.name()),
                    exception.getMessage()
            );

            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status successfully")
        void shouldUpdateOrderStatusSuccessfully() {
            // Given
            UUID orderId = UUID.randomUUID();

            Order order = Order.builder()
                    .id(orderId)
                    .status(OrderStatus.PENDING_PAYMENT)
                    .build();

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // When
            orderService.updateOrderStatus(orderId, OrderStatus.PAID);

            // Then
            assertEquals(OrderStatus.PAID, order.getStatus());

            verify(orderRepository).findById(orderId);
        }

        @Test
        @DisplayName("Should throw exception when order is not found")
        void shouldThrowExceptionWhenOrderIsNotFound() {
            // Given
            UUID orderId = UUID.randomUUID();

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // When & Then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> orderService.updateOrderStatus(orderId, OrderStatus.PAID)
            );

            assertEquals(ORDER_NOT_FOUND.getDefaultMessage(), exception.getMessage());

            verify(orderRepository).findById(orderId);
        }
    }

    @Nested
    @DisplayName("OrderStatusValidator Unit Tests")
    class OrderStatusValidatorTest {

        private final OrderStatusValidator validator = new OrderStatusValidator();

        @Test
        void shouldAllowPendingPaymentToPaid() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.PENDING_PAYMENT,
                            OrderStatus.PAID
                    )
            );
        }

        @Test
        void shouldAllowPendingPaymentToCancelled() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.PENDING_PAYMENT,
                            OrderStatus.CANCELLED
                    )
            );
        }

        @Test
        void shouldAllowPaidToProcessing() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.PAID,
                            OrderStatus.PROCESSING
                    )
            );
        }

        @Test
        void shouldAllowPaidToCancelled() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.PAID,
                            OrderStatus.CANCELLED
                    )
            );
        }

        @Test
        void shouldAllowProcessingToShipped() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.PROCESSING,
                            OrderStatus.SHIPPED
                    )
            );
        }

        @Test
        void shouldAllowShippedToDelivered() {
            assertDoesNotThrow(() ->
                    validator.validateTransition(
                            OrderStatus.SHIPPED,
                            OrderStatus.DELIVERED
                    )
            );
        }

        @Test
        void shouldThrowExceptionForInvalidTransition() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> validator.validateTransition(
                            OrderStatus.PENDING_PAYMENT,
                            OrderStatus.DELIVERED
                    )
            );

            assertEquals(
                    String.format(
                            INVALID_ORDER_STATUS_TRANSITION.getDefaultMessage(),
                            OrderStatus.PENDING_PAYMENT.name(),
                            OrderStatus.DELIVERED.name()
                    ),
                    exception.getMessage()
            );
        }
    }
}