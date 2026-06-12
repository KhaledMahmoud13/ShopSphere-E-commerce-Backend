package com.khaled.shopsphere.cart;

import com.khaled.shopsphere.cart.request.AddCartItemRequest;
import com.khaled.shopsphere.cart.request.UpdateCartItemRequest;
import com.khaled.shopsphere.cart.response.CartResponse;

import java.util.UUID;

public interface CartService {
    void addItem(AddCartItemRequest request, UUID userId);

    CartResponse getMyCart(UUID userId);

    void updateQuantity(UUID productId, UpdateCartItemRequest request, UUID userId);

    void removeItem(UUID productId, UUID userId);

    void clear(UUID userId);
}
