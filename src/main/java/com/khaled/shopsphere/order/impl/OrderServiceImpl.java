package com.khaled.shopsphere.order.impl;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.inventory.InventoryItem;
import com.khaled.shopsphere.inventory.InventoryService;
import com.khaled.shopsphere.order.*;
import com.khaled.shopsphere.order.event.OrderCreatedEvent;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper mapper;
    private final OrderStatusValidator orderStatusValidator;

    @Override
    @Transactional
    public Order createFromCart(List<OrderItemRequest> orderItems, UUID userId) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException(ORDER_HAS_NO_ITEMS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        List<InventoryItem> inventoryItems = orderItems.stream()
                .map(item -> new InventoryItem(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        inventoryService.validateAndDeduct(inventoryItems);

        Set<UUID> productIds = orderItems.stream()
                .map(OrderItemRequest::getProductId)
                .collect(Collectors.toSet());

        Map<UUID, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(
                        Product::getId, p -> p
                ));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        List<OrderItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderItems) {
            Product product = productMap.get(itemRequest.getProductId());

            if (product == null) {
                throw new BusinessException(PRODUCT_NOT_FOUND);
            }

            if (itemRequest.getQuantity() <= 0) {
                throw new BusinessException(INVALID_ITEM_QUANTITY);
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    itemRequest.getQuantity()
                            )
                    );

            total = total.add(itemTotal);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            items.add(item);
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getId(), userId));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        return orderRepository.findByUserId(userId)
                .stream()
                .map(mapper::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(ORDER_ALREADY_CANCELLED);
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ORDER_CANNOT_BE_CANCELLED, order.getStatus().name());
        }

        inventoryService.restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ORDER_NOT_FOUND));

        orderStatusValidator.validateTransition(order.getStatus(), status);
        order.setStatus(status);
    }
}
