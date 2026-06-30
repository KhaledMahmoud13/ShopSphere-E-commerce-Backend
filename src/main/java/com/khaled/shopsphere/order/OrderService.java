package com.khaled.shopsphere.order;

import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.order.request.OrderFilterRequest;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.user.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createFromCart(List<OrderItemRequest> orderItems, User user);

    PageResponse<OrderResponse> getUserOrders(UUID userId, Pageable pageable);

    OrderResponse getOrderById(UUID orderId);

    void cancelOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus status);

    PageResponse<OrderResponse> getAllOrders(OrderFilterRequest filter, Pageable pageable);
}
