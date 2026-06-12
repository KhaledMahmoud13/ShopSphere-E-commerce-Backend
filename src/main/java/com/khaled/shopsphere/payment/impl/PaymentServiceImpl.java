package com.khaled.shopsphere.payment.impl;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.exception.ErrorCode;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderRepository;
import com.khaled.shopsphere.order.OrderStatus;
import com.khaled.shopsphere.payment.*;
import com.khaled.shopsphere.payment.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public Payment create(Order order) {

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new BusinessException(PAYMENT_ALREADY_EXISTS);
        }

        return paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .amount(order.getTotalPrice())
                        .status(PaymentStatus.PENDING)
                        .build()
        );
    }

    @Override
    @Transactional
    public PaymentResponse create(UUID orderId) {
        final Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ORDER_NOT_FOUND));
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .build();

        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponse markSucceeded(UUID orderId) {
        final Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new BusinessException(PAYMENT_NOT_FOUND));

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.getOrder().setStatus(OrderStatus.PAID);

        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse markFailed(UUID orderId) {
        final Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new BusinessException(PAYMENT_NOT_FOUND));

        payment.setStatus(PaymentStatus.SUCCEEDED);

        return paymentMapper.toPaymentResponse(payment);
    }
}
