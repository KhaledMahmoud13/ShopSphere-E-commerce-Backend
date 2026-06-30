package com.khaled.shopsphere.cart.impl;

import com.khaled.shopsphere.cart.*;
import com.khaled.shopsphere.cart.request.AddCartItemRequest;
import com.khaled.shopsphere.cart.request.UpdateCartItemRequest;
import com.khaled.shopsphere.cart.response.CartResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import com.khaled.shopsphere.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public void addItem(AddCartItemRequest request, User user) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .build();

            return cartRepository.save(newCart);
        });

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct()
                        .getId()
                        .equals(product.getId())
                ).findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() ->
                        Cart.builder()
                                .user(user)
                                .build()
                );

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public void updateQuantity(UUID productId, UpdateCartItemRequest request, UUID userId) {
        Cart cart = getCart(userId);

        CartItem item = cart.getItems()
                .stream()
                .filter(i -> i.getProduct()
                        .getId()
                        .equals(productId)
                ).findFirst()
                .orElseThrow(() -> new BusinessException(CART_ITEM_NOT_FOUND));

        item.setQuantity(request.getQuantity());
    }

    @Override
    @Transactional
    public void removeItem(UUID productId, UUID userId) {
        Cart cart = getCart(userId);

        boolean removed = cart.getItems().removeIf(item ->
                item.getProduct()
                        .getId()
                        .equals(productId)
        );

        if (!removed) throw new BusinessException(CART_ITEM_NOT_FOUND);
    }

    @Override
    @Transactional
    public void clear(UUID userId) {
        Cart cart = getCart(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    private Cart getCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CART_NOT_FOUND, userId));
    }
}
