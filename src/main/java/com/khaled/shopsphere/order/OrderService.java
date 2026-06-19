package com.khaled.shopsphere.order;

import com.khaled.shopsphere.cart.Cart;
import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.order.request.CreateOrderRequest;
import com.khaled.shopsphere.order.request.OrderFilterRequest;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createFromCart(List<OrderItemRequest> orderItems, UUID userId);

    List<OrderResponse> getUserOrders(UUID userId);

    OrderResponse getOrderById(UUID orderId);

    void cancelOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus status);

    PageResponse<OrderResponse> getAllOrders(OrderFilterRequest filter, Pageable pageable);
}
