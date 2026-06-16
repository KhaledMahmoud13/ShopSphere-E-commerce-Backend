package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.request.CheckoutRequest;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;

import java.util.UUID;

public interface CheckoutService {
    CheckoutResponse checkout(UUID userId, CheckoutRequest request);
}
