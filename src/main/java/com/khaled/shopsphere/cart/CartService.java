package com.khaled.shopsphere.cart;

import com.khaled.shopsphere.cart.request.AddCartItemRequest;
import com.khaled.shopsphere.cart.request.UpdateCartItemRequest;
import com.khaled.shopsphere.cart.response.CartResponse;
import com.khaled.shopsphere.user.User;

import java.util.UUID;

public interface CartService {
    void addItem(AddCartItemRequest request, User user);

    CartResponse getMyCart(User user);

    void updateQuantity(UUID productId, UpdateCartItemRequest request, UUID userId);

    void removeItem(UUID productId, UUID userId);

    void clear(UUID userId);
}
