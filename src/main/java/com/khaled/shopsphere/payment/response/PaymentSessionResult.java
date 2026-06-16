package com.khaled.shopsphere.payment.response;

import com.khaled.shopsphere.payment.Payment;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSessionResult {
    private Payment payment;
    private String sessionUrl;
    private String sessionId;
}
