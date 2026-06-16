package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;

import java.util.UUID;

public interface PaymentService {
    PaymentSessionResult createStripeSession(Order order);

    void markSucceeded(String paymentIntentId);

    void markFailed(String paymentIntentId);

    void attachPaymentIntent(String stripeSessionId, String paymentIntentId);

    PaymentSessionResult getOrCreateSession(UUID orderId);
}
