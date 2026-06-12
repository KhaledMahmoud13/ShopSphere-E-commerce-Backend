package com.khaled.shopsphere.order.request;

import com.khaled.shopsphere.order.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
