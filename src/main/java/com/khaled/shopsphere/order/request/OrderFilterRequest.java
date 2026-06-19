package com.khaled.shopsphere.order.request;

import com.khaled.shopsphere.order.OrderStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderFilterRequest {
    private OrderStatus status;
    @Size(max = 3, message = "You can sort by at most 4 fields")
    private List<
            @Pattern(
                    regexp = "totalPrice|status|createdDate|lastModifiedDate",
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
}