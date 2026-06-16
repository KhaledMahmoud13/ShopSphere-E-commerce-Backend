package com.khaled.shopsphere.payment;

import com.khaled.shopsphere.common.BaseEntity;
import com.khaled.shopsphere.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "PAYMENTS")
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, unique = true)
    private Order order;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "STRIPE_SESSION_ID", unique = true)
    private String stripeSessionId;

    @Column(name = "STRIPE_PAYMENT_INTENT_ID", unique = true)
    private String stripePaymentIntentId;

    @Column(name = "PAID_AT")
    private Instant paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PaymentStatus status;
}
