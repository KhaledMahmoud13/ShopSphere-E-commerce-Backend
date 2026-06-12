package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.payment.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    Payment create(Order order);

    PaymentResponse create(UUID orderId);

    PaymentResponse markSucceeded(UUID orderId);

    PaymentResponse markFailed(UUID orderId);
}
