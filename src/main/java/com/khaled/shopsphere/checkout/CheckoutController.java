package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.request.CheckoutRequest;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Checkout API")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    @PreAuthorize("hasAuthority('checkout:create')")
    public ResponseEntity<CheckoutResponse> checkout(
            @Valid @RequestBody CheckoutRequest request,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(checkoutService.checkout(userId, request));
    }
}
