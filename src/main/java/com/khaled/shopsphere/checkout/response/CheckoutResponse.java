package com.khaled.shopsphere.checkout.response;

import com.khaled.shopsphere.order.OrderStatus;
import com.khaled.shopsphere.payment.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResponse {
    private UUID orderId;
    private OrderStatus status;
    private Integer totalItems;
    private BigDecimal totalPrice;
    private UUID paymentId;
    private PaymentStatus paymentStatus;
    private String sessionUrl;
    private List<CheckoutItemResponse> items;
}
