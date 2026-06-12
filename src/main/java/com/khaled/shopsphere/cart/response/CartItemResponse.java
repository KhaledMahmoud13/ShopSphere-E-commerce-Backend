package com.khaled.shopsphere.cart.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private UUID productId;

    private String productName;

    private String imageUrl;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}
