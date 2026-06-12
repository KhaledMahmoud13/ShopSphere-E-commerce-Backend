package com.khaled.shopsphere.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCartItemRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
