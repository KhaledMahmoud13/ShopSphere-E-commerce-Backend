package com.khaled.shopsphere.inventory;

import java.util.UUID;

public record InventoryItem(
        UUID productId,
        int quantity
) {}
