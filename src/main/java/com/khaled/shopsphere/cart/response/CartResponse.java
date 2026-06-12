package com.khaled.shopsphere.cart.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private UUID id;

    private Integer totalItems;

    private BigDecimal totalPrice;

    private List<CartItemResponse> items;
}
