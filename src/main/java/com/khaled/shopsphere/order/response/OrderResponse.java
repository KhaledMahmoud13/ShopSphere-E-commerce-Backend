package com.khaled.shopsphere.order.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private UUID id;
    private BigDecimal totalPrice;
    private String status;
    private OrderAddressResponse shippingAddress;
    private List<OrderItemResponse> items;
}
