package com.khaled.shopsphere.payment.response;

import com.khaled.shopsphere.payment.PaymentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSessionResponse {
    private UUID paymentId;
    private String sessionUrl;
    private PaymentStatus status;
}
