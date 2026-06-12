package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.response.CheckoutItemResponse;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderItem;
import com.khaled.shopsphere.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutMapper {

    public CheckoutResponse toCheckoutResponse(Order order, Payment payment) {
        List<CheckoutItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        Integer totalItems = order.getItems()
                .stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus().name())
                .totalItems(totalItems)
                .items(items)
                .build();
    }

    private CheckoutItemResponse toItemResponse(OrderItem item) {
//        BigDecimal subTotal = item.getPriceAtPurchase()
//                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CheckoutItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .price(item.getPriceAtPurchase())
//                .subtotal(subTotal)
                .build();
    }
}
