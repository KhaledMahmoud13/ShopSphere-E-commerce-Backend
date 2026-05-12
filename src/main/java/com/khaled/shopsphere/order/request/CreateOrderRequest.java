package com.khaled.shopsphere.order.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {
    private List<OrderItemRequest> items;
}
