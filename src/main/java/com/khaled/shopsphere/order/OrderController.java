package com.khaled.shopsphere.order;

import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.order.request.OrderFilterRequest;
import com.khaled.shopsphere.order.request.UpdateOrderStatusRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order API")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('order:read')")
    public PageResponse<OrderResponse> getMyOrders(
            Authentication authentication,
            Pageable pageable
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        return orderService.getUserOrders(userId, pageable);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("""
            hasAuthority('order:read')
            and
            @orderSecurityService.isOrderOwner(#orderId)
            """)
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("""
            hasAuthority('order:read')
            and
            @orderSecurityService.isOrderOwner(#orderId)
            """)
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId
    ) {
        orderService.cancelOrder(orderId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('admin:access')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {

        orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin:access')")
    public PageResponse<OrderResponse> getAllOrders(
            @Valid OrderFilterRequest filter,
            Pageable pageable
    ) {
        return orderService.getAllOrders(filter, pageable);
    }
}
