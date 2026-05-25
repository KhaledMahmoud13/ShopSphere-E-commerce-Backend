package com.khaled.shopsphere.order;

import com.khaled.shopsphere.order.request.CreateOrderRequest;
import com.khaled.shopsphere.order.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request, UUID userId);
    List<OrderResponse> getUserOrders(UUID userId);
}
