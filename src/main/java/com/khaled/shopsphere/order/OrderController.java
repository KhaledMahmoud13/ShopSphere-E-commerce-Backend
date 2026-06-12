package com.khaled.shopsphere.order;

import com.khaled.shopsphere.order.request.CreateOrderRequest;
import com.khaled.shopsphere.order.request.UpdateOrderStatusRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order API")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication
    ) {

        UUID userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAuthority('order:read')")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {

        UUID userId = ((User) authentication.getPrincipal()).getId();


        return ResponseEntity.ok(orderService.getOrderById(userId, orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAuthority('order:cancel')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {

        UUID userId = ((User) authentication.getPrincipal()).getId();

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('order:update-status')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication) {

        orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.noContent().build();
    }
}
