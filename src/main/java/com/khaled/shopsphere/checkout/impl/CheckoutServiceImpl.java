package com.khaled.shopsphere.checkout.impl;

import com.khaled.shopsphere.address.Address;
import com.khaled.shopsphere.address.AddressRepository;
import com.khaled.shopsphere.cart.Cart;
import com.khaled.shopsphere.cart.CartRepository;
import com.khaled.shopsphere.checkout.CheckoutMapper;
import com.khaled.shopsphere.checkout.CheckoutService;
import com.khaled.shopsphere.checkout.request.CheckoutRequest;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderAddress;
import com.khaled.shopsphere.order.OrderAddressRepository;
import com.khaled.shopsphere.order.OrderService;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.payment.Payment;
import com.khaled.shopsphere.payment.PaymentService;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final OrderAddressRepository orderAddressRepository;
    private final CheckoutMapper checkoutMapper;

    @Override
    @Transactional
    public CheckoutResponse checkout(UUID userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CART_IS_EMPTY));

        Address address = addressRepository
                .findByIdAndUserId(
                        request.getAddressId(),
                        userId
                )
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND));

        List<OrderItemRequest> items = cart.getItems()
                .stream()
                .map(item -> OrderItemRequest.builder()
                        .productId(item.getProduct().getId())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        Order order = orderService.createFromCart(items, userId);

        OrderAddress shippingAddress = OrderAddress.builder()
                .order(order)
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .country(address.getCountry())
                .city(address.getCity())
                .area(address.getArea())
                .street(address.getStreet())
                .buildingNumber(address.getBuildingNumber())
                .floorNumber(address.getFloorNumber())
                .apartmentNumber(address.getApartmentNumber())
                .postalCode(address.getPostalCode())
                .build();

        orderAddressRepository.save(shippingAddress);

        PaymentSessionResult sessionResult = paymentService.createStripeSession(order);

        cart.getItems().clear();

        return checkoutMapper.toCheckoutResponse(order, sessionResult);
    }
}
