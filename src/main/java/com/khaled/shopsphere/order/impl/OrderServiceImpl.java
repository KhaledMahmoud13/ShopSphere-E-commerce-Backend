package com.khaled.shopsphere.order.impl;

import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.inventory.InventoryItem;
import com.khaled.shopsphere.inventory.InventoryService;
import com.khaled.shopsphere.order.*;
import com.khaled.shopsphere.order.event.OrderCreatedEvent;
import com.khaled.shopsphere.order.request.OrderFilterRequest;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper mapper;
    private final OrderStatusValidator orderStatusValidator;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("totalPrice", "status", "createdDate", "lastModifiedDate");

    @Override
    @Transactional
    public Order createFromCart(List<OrderItemRequest> orderItems, User user) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new BusinessException(ORDER_HAS_NO_ITEMS);
        }

        List<InventoryItem> inventoryItems = orderItems.stream()
                .map(item -> new InventoryItem(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        Map<UUID, Product> productMap = inventoryService.validateAndDeduct(inventoryItems);

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
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

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

        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getId(), user.getId()));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(UUID userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(userId, pageable);
        return getOrderResponsePageResponse(page);
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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(OrderFilterRequest filter, Pageable pageable) {
        Pageable finalPageable = pageable;

        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {

            Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            List<Sort.Order> orders = filter.getSortBy().stream()
                    .filter(ALLOWED_SORT_FIELDS::contains)
                    .map(field -> new Sort.Order(direction, field))
                    .toList();

            finalPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(orders)
            );
        }

        Page<Order> page;

        if (filter.getStatus() != null) {
            page = orderRepository.findByStatus(filter.getStatus(), finalPageable);
        } else {
            page = orderRepository.findAll(finalPageable);
        }

        return getOrderResponsePageResponse(page);
    }

    private PageResponse<OrderResponse> getOrderResponsePageResponse(Page<Order> page) {
        Page<OrderResponse> responsePage = page.map(mapper::toOrderResponse);
        return PageResponse.<OrderResponse>builder()
                .data(responsePage.getContent())
                .page(responsePage.getNumber() + 1)
                .size(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .first(responsePage.isFirst())
                .last(responsePage.isLast())
                .build();
    }
}
