package com.khaled.shopsphere.cart.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCartItemRequest {
    @NotNull
    @Min(1)
    private Integer quantity;
}
