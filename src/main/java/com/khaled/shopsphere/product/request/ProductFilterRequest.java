package com.khaled.shopsphere.product.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductFilterRequest {
    @Size(max = 100, message = "Search term must be at most 100 characters")
    private String search;
    @DecimalMin(value = "0.0", inclusive = true, message = "minPrice must be >= 0")
    private BigDecimal minPrice;
    @DecimalMin(value = "0.0", inclusive = true, message = "maxPrice must be >= 0")
    private BigDecimal maxPrice;
    @Min(value = 0, message = "minStock must be >= 0")
    private Integer minStock;
    @Min(value = 0, message = "maxStock must be >= 0")
    private Integer maxStock;
    private UUID categoryId;
    @Size(max = 5, message = "You can sort by at most 5 fields")
    private List<
            @Pattern(
                    regexp = "name|price|stock|createdDate",
                    message = "Invalid sort field"
            )
                    String
            > sortBy;
    @Pattern(
            regexp = "asc|desc",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "sortDirection must be 'asc' or 'desc'"
    )
    private String sortDirection;

    @AssertTrue(message = "minPrice must be less than or equal to maxPrice")
    public boolean isPriceRangeValid() {
        return minPrice == null
                || maxPrice == null
                || minPrice.compareTo(maxPrice) <= 0;
    }

    @AssertTrue(message = "minStock must be less than or equal to maxStock")
    public boolean isStockRangeValid() {
        return minStock == null
                || maxStock == null
                || minStock <= maxStock;
    }
}
