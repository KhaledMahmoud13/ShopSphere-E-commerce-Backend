package com.khaled.shopsphere.checkout.impl;

import com.khaled.shopsphere.address.AddressRepository;
import com.khaled.shopsphere.cart.CartItemRepository;
import com.khaled.shopsphere.cart.CartRepository;
import com.khaled.shopsphere.checkout.CheckoutMapper;
import com.khaled.shopsphere.order.OrderAddressRepository;
import com.khaled.shopsphere.order.OrderService;
import com.khaled.shopsphere.payment.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutServiceImpl Unit Tests")
class CheckoutServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderAddressRepository orderAddressRepository;
    @Mock
    private CheckoutMapper checkoutMapper;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;
}