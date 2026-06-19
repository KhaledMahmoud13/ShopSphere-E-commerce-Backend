package com.khaled.shopsphere.order;

import com.khaled.shopsphere.order.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserId(UUID userId);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
