package com.khaled.shopsphere.order;

import com.khaled.shopsphere.order.request.CreateOrderRequest;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order API")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('order:create')")
    public OrderResponse create(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {

        UUID userId = ((User) authentication.getPrincipal()).getId();
        return orderService.create(request, userId);
    }
}
