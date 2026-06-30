package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.payment.response.PaymentSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMapper {

    public PaymentSessionResponse toResponse(
            Payment payment,
            String sessionUrl
    ) {
        return PaymentSessionResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus())
                .sessionUrl(sessionUrl)
                .build();
    }
}
