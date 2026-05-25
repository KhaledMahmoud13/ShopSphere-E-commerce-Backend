package com.khaled.shopsphere.order.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
    private UUID productId;
    private Integer quantity;
}
