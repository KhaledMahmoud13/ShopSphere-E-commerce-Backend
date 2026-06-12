package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.response.CheckoutResponse;

import java.util.UUID;

public interface CheckoutService {
    CheckoutResponse checkout(UUID userId);
}
