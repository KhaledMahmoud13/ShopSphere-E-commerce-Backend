package com.khaled.shopsphere.payment.impl;

import com.khaled.shopsphere.config.StripeProperties;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.payment.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.khaled.shopsphere.exception.ErrorCode.INVALID_STRIPE_SIGNATURE;
import static com.khaled.shopsphere.exception.ErrorCode.STRIPE_EVENT_DESERIALIZATION_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {
    private final StripeProperties stripeProperties;
    private final PaymentService paymentService;

    public void processWebhook(
            String payload,
            String signature
    ) {

        Event event = validateEvent(payload, signature);

        log.info("Received Stripe event: {}", event.getType());

        switch (event.getType()) {

            case "checkout.session.completed" -> handleCheckoutCompleted(event);

            case "payment_intent.payment_failed" -> handlePaymentFailed(event);

            default -> log.debug("Unhandled Stripe event {}", event.getType());
        }
    }

    private Event validateEvent(
            String payload,
            String signature
    ) {
        try {
            return Webhook.constructEvent(payload, signature, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe webhook signature: {}", e.getMessage());
            throw new BusinessException(INVALID_STRIPE_SIGNATURE);
        }
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = extractSession(event);

        paymentService.handleCheckoutCompleted(session.getId(), session.getPaymentIntent());
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent intent = extractPaymentIntent(event);
        paymentService.markFailed(intent.getId());
    }

    private Session extractSession(Event event) {
        return (Session) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new BusinessException(STRIPE_EVENT_DESERIALIZATION_FAILED));
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        return (PaymentIntent) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new BusinessException(STRIPE_EVENT_DESERIALIZATION_FAILED));
    }

}
