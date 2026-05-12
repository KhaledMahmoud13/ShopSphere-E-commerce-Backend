package com.khaled.shopsphere.inventory;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.order.request.OrderItemRequest;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.khaled.shopsphere.exception.ErrorCode.ITEM_OUT_OF_STOCK;
import static com.khaled.shopsphere.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InventoryService {
    final ProductRepository productRepository;

    public void validateAndDeduct(
            List<OrderItemRequest> items
    ) {
        for (OrderItemRequest item : items) {

            Product product = productRepository.findByIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND));

            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException(ITEM_OUT_OF_STOCK, product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
        }
    }
}
