package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.payment.response.PaymentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment API")
public class PaymentController {
    private final PaymentService paymentService;

//    @PostMapping("orders/{orderId}")
//    @PreAuthorize("hasAuthority('payment:create')")
//    public ResponseEntity<PaymentResponse> create(@PathVariable UUID orderId) {
//        return ResponseEntity.ok(paymentService.create(orderId));
//    }

    @PostMapping("orders/{orderId}/success")
    @PreAuthorize("hasAuthority('payment:create')")
    public ResponseEntity<PaymentResponse> success(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.markSucceeded(orderId));
    }

    @PostMapping("orders/{orderId}/failed")
    @PreAuthorize("hasAuthority('payment:create')")
    public ResponseEntity<PaymentResponse> failed(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.markFailed(orderId));
    }
}
