package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.payment.response.PaymentSessionResponse;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment API")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/session/{orderId}")
    @PreAuthorize("""
            hasAuthority('payment:create')
            and
            @paymentSecurityService.canPayOrder(#orderId)
            """)
    public ResponseEntity<PaymentSessionResponse> getOrCreateSession(@PathVariable UUID orderId) {
        PaymentSessionResult result = paymentService.getOrCreateSession(orderId);
        return ResponseEntity.ok(
                PaymentSessionResponse.builder()
                        .paymentId(result.getPayment().getId())
                        .sessionUrl(result.getSessionUrl())
                        .status(result.getPayment().getStatus())
                        .build()
        );
    }
}
