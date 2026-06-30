package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.request.CheckoutRequest;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.user.User;

public interface CheckoutService {
    CheckoutResponse checkout(User user, CheckoutRequest request);
}
