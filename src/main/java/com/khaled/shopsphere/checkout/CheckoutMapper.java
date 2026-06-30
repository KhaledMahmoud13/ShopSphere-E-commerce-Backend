package com.khaled.shopsphere.checkout;

import com.khaled.shopsphere.checkout.response.CheckoutItemResponse;
import com.khaled.shopsphere.checkout.response.CheckoutResponse;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderItem;
import com.khaled.shopsphere.payment.response.PaymentSessionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutMapper {

    public CheckoutResponse toCheckoutResponse(Order order, PaymentSessionResult result) {
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
                .paymentId(result.getPayment().getId())
                .paymentStatus(result.getPayment().getStatus())
                .sessionUrl(result.getSessionUrl())
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
