package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.payment.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMapper {
    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .build();
    }
}
