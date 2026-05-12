package com.khaled.shopsphere.order.event;

import java.util.UUID;

public record OrderCreatedEvent(UUID orderId, UUID userId) {
}
