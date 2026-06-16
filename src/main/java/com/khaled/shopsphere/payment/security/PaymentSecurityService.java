package com.khaled.shopsphere.payment.security;

import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderRepository;
import com.khaled.shopsphere.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentSecurityService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public boolean canPayOrder(UUID orderId) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        UUID userId = ((User) authentication.getPrincipal()).getId();

        Order order = orderRepository.findById(orderId).orElse(null);

        return order != null && order.getUser().getId().equals(userId);
    }
}
