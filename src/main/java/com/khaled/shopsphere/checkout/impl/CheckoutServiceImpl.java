package com.khaled.shopsphere.checkout.impl;

import com.khaled.shopsphere.cart.Cart;
import com.khaled.shopsphere.cart.CartRepository;
import com.khaled.shopsphere.checkout.CheckoutMapper;
import com.khaled.shopsphere.checkout.CheckoutService;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderService;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.payment.Payment;
import com.khaled.shopsphere.payment.PaymentService;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.CART_IS_EMPTY;
import static com.khaled.shopsphere.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CheckoutMapper checkoutMapper;

    @Override
    @Transactional
    public CheckoutResponse checkout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CART_IS_EMPTY));

        List<OrderItemRequest> items = cart.getItems()
                .stream()
                .map(item -> OrderItemRequest.builder()
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        Order order = orderService.createFromCart(items, userId);

         Payment payment = paymentService.create(order);

        cart.getItems().clear();

        return checkoutMapper.toCheckoutResponse(order, payment);
    }
}
