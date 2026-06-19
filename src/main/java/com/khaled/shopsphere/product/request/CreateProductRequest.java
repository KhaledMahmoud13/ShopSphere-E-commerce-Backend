package com.khaled.shopsphere.product.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductRequest {
    @NotBlank(message = "VALIDATION.PRODUCT.NAME.NOT_BLANK")
    @Size(min = 3, max = 100, message = "VALIDATION.PRODUCT.NAME.SIZE")
    private String name;
    @Size(max = 1000, message = "VALIDATION.PRODUCT.DESCRIPTION.SIZE")
    private String description;
    @NotNull(message = "VALIDATION.PRODUCT.PRICE.NOT_NULL")
    @DecimalMin(value = "0.0", inclusive = false, message = "VALIDATION.PRODUCT.PRICE.MIN")
    @Digits(integer = 10, fraction = 2, message = "VALIDATION.PRODUCT.PRICE.DIGITS")
    private BigDecimal price;
    @NotNull(message = "VALIDATION.PRODUCT.STOCK.NOT_NULL")
    @Min(value = 0, message = "VALIDATION.PRODUCT.STOCK.MIN")
    private Integer stock;
    @NotNull(message = "VALIDATION.PRODUCT.CATEGORY_ID.NOT_NULL")
    private UUID categoryId;
}
