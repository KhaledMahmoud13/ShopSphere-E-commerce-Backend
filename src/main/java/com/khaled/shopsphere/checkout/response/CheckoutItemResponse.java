package com.khaled.shopsphere.checkout.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutItemResponse {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
//    private BigDecimal subtotal;
}
