package com.khaled.shopsphere.inventory;

import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.product.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface InventoryService {

    Map<UUID, Product> validateAndDeduct(List<InventoryItem> items);

    void restoreStock(Order order);
}
