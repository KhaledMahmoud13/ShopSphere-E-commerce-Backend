package com.khaled.shopsphere.order;

import com.khaled.shopsphere.order.response.OrderItemResponse;
import com.khaled.shopsphere.order.response.OrderResponse;
import com.khaled.shopsphere.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderMapper {

    public OrderResponse toOrderResponse(
            Order order,
            Map<UUID, Product> productMap
    ) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> {

                    Product product = productMap.get(item.getProductId());

                    return OrderItemResponse.builder()
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .quantity(item.getQuantity())
                            .price(item.getPriceAtPurchase())
//                            .currentPrice(
//                                    product != null
//                                            ? product.getPrice()
//                                            : null
//                            )
                            .build();
                })
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .items(items)
                .build();
    }

    public OrderResponse toOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .items(items)
                .build();
    }
}
