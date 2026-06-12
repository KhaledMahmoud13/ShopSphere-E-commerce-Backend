package com.khaled.shopsphere.cart;

import com.khaled.shopsphere.cart.request.AddCartItemRequest;
import com.khaled.shopsphere.cart.request.UpdateCartItemRequest;
import com.khaled.shopsphere.cart.response.CartResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart API")
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('cart:write')")
    public void addItem(
            @Valid @RequestBody AddCartItemRequest request,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        cartService.addItem(request, userId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('cart:read')")
    public ResponseEntity<CartResponse> getMyCart(
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    @PatchMapping("/items/{productId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('cart:write')")
    public void updateQuantity(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        cartService.updateQuantity(productId, request, userId);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('cart:write')")
    public void removeItem(
            @PathVariable UUID productId,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        cartService.removeItem(productId, userId);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('cart:write')")
    public void clear(Authentication authentication) {
        UUID userId = ((User) authentication.getPrincipal()).getId();
        cartService.clear(userId);
    }
}
