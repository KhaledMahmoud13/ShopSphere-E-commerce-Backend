package com.khaled.shopsphere.inventory.impl;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.inventory.InventoryItem;
import com.khaled.shopsphere.inventory.InventoryService;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderItem;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.khaled.shopsphere.exception.ErrorCode.ITEM_OUT_OF_STOCK;
import static com.khaled.shopsphere.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    final ProductRepository productRepository;

    @Override
    public Map<UUID, Product> validateAndDeduct(List<InventoryItem> items) {
        List<UUID> ids = items.stream().map(InventoryItem::productId).toList();
        Map<UUID, Product> productMap = productRepository.findAllByIdForUpdate(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (InventoryItem item : items) {
            Product product = productMap.get(item.productId());
            if (product == null) throw new BusinessException(PRODUCT_NOT_FOUND);
            if (product.getStock() < item.quantity())
                throw new BusinessException(ITEM_OUT_OF_STOCK, product.getName());
            product.setStock(product.getStock() - item.quantity());
        }

        return productMap;
    }

    @Override
    public void restoreStock(Order order) {
        List<UUID> ids = order.getItems()
                .stream()
                .map(OrderItem::getProductId)
                .toList();

        Map<UUID, Product> productMap = productRepository.findAllByIdForUpdate(ids)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (OrderItem item : order.getItems()) {
            Product product = productMap.get(item.getProductId());

            if (product == null) {
                log.warn("Product {} not found while restoring stock", item.getProductId());
                continue;
            }

            product.setStock(product.getStock() + item.getQuantity());
        }
    }
}
