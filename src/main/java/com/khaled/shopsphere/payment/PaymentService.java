package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;

import java.util.UUID;

public interface PaymentService {
    PaymentSessionResult createStripeSession(Order order);

    void handleCheckoutCompleted(String sessionId, String paymentIntentId);

    void markFailed(String paymentIntentId);

    PaymentSessionResult getOrCreateSession(UUID orderId);
}
