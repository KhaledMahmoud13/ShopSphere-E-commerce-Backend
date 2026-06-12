package com.khaled.shopsphere.order;

import com.khaled.shopsphere.exception.BusinessException;
import org.springframework.stereotype.Component;

import static com.khaled.shopsphere.exception.ErrorCode.INVALID_ORDER_STATUS_TRANSITION;
import static com.khaled.shopsphere.order.OrderStatus.*;

@Component
public class OrderStatusValidator {
    public void validateTransition(OrderStatus current, OrderStatus target) {
        boolean valid = switch (current) {
            case CREATED -> target == PAID || target == OrderStatus.CANCELLED;
            case PAID -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED;
            case SHIPPED -> target == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new BusinessException(INVALID_ORDER_STATUS_TRANSITION, current.name(), target.name());
        }
    }
}
