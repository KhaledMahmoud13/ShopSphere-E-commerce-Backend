package com.khaled.shopsphere.cart;

import com.khaled.shopsphere.cart.response.CartItemResponse;
import com.khaled.shopsphere.cart.response.CartResponse;
import com.khaled.shopsphere.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartMapper {
    public CartResponse toCartResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = items.stream()
                .map(CartItemResponse::getQuantity)
                .reduce(0, Integer::sum);

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {

        Product product = item.getProduct();

        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getPrimaryImageUrl())
                .quantity(item.getQuantity())
                .price(product.getPrice())
                .subtotal(subtotal)
                .build();
    }
}
