package com.khaled.shopsphere.inventory.impl;

import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.inventory.InventoryItem;
import com.khaled.shopsphere.order.Order;
import com.khaled.shopsphere.order.OrderItem;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceImpl Unit Tests")
class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private UUID testProduct1Id;
    private UUID testProduct2Id;
    private UUID testProduct3Id;
    private UUID testProduct4Id;

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;
    private Product testProduct4;

    @BeforeEach
    void setup() {
        this.testProduct1Id = UUID.randomUUID();
        this.testProduct2Id = UUID.randomUUID();
        this.testProduct3Id = UUID.randomUUID();
        this.testProduct4Id = UUID.randomUUID();

        this.testProduct1 = Product.builder()
                .id(testProduct1Id)
                .name("Test Product 1")
                .description("Test Description 1")
                .price(BigDecimal.valueOf(100.99))
                .stock(100)
                .build();

        this.testProduct2 = Product.builder()
                .id(testProduct2Id)
                .name("Test Product 2")
                .description("Test Description 2")
                .price(BigDecimal.valueOf(10.99))
                .stock(10)
                .build();

        this.testProduct3 = Product.builder()
                .id(testProduct3Id)
                .name("Test Product 3")
                .description("Test Description 3")
                .price(BigDecimal.valueOf(200.99))
                .stock(50)
                .build();

        this.testProduct4 = Product.builder()
                .id(testProduct4Id)
                .name("Test Product 4")
                .description("Test Description 4")
                .price(BigDecimal.valueOf(500))
                .stock(5)
                .build();
    }

    @Nested
    @DisplayName("Validate and Deduct Tests")
    class ValidateAndDeductTests {

        @Test
        @DisplayName("Should deduct stock quantity when stock is available")
        void shouldDeductStockQuantityWhenStockIsAvailable() {
            // Given
            List<InventoryItem> inventoryItems = List.of(
                    new InventoryItem(testProduct1Id, 5),
                    new InventoryItem(testProduct2Id, 5),
                    new InventoryItem(testProduct3Id, 5),
                    new InventoryItem(testProduct4Id, 5)
            );
            when(productRepository.findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id, testProduct3Id, testProduct4Id)))
                    .thenReturn(List.of(testProduct1, testProduct2, testProduct3, testProduct4));

            // When
            Map<UUID, Product> result = inventoryService.validateAndDeduct(inventoryItems);

            // Then
            assertEquals(95, testProduct1.getStock());
            assertEquals(5, testProduct2.getStock());
            assertEquals(45, testProduct3.getStock());
            assertEquals(0, testProduct4.getStock());

            assertEquals(4, result.size());
            assertEquals(testProduct1, result.get(testProduct1Id));
            assertEquals(testProduct2, result.get(testProduct2Id));
            assertEquals(testProduct3, result.get(testProduct3Id));
            assertEquals(testProduct4, result.get(testProduct4Id));

            verify(productRepository).findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id, testProduct3Id, testProduct4Id));
        }

        @Test
        @DisplayName("Should throw exception when product is out of stock")
        void shouldThrowExceptionWhenProductIsOutOfStock() {
            // Given
            List<InventoryItem> inventoryItems = List.of(new InventoryItem(testProduct4Id, 6));

            when(productRepository.findAllByIdForUpdate(List.of(testProduct4Id))).thenReturn(List.of(testProduct4));

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> inventoryService.validateAndDeduct(inventoryItems)
            );

            assertEquals("Product '" + testProduct4.getName() + "' is out of stock", exception.getMessage());
            verify(productRepository).findAllByIdForUpdate(List.of(testProduct4Id));
        }

        @Test
        @DisplayName("Should throw exception when product is not found")
        void shouldThrowExceptionWhenProductIsNotFound() {
            // Given
            List<InventoryItem> inventoryItems = List.of(
                    new InventoryItem(testProduct1Id, 5),
                    new InventoryItem(testProduct2Id, 5),
                    new InventoryItem(testProduct3Id, 5),
                    new InventoryItem(testProduct4Id, 5)
            );
            when(productRepository.findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id, testProduct3Id, testProduct4Id)))
                    .thenReturn(List.of(testProduct1, testProduct2, testProduct3));

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> inventoryService.validateAndDeduct(inventoryItems)
            );
            assertEquals("Product not found", exception.getMessage());
            verify(productRepository).findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id, testProduct3Id, testProduct4Id));
        }
    }

    @Nested
    @DisplayName("Restore Stock Tests")
    class RestoreStockTests {

        @Test
        @DisplayName("Should restore stock for all order items")
        void shouldRestoreStockForAllOrderItems() {
            // Given
            testProduct1.setStock(95);
            testProduct2.setStock(5);

            OrderItem item1 = OrderItem.builder()
                    .productId(testProduct1Id)
                    .quantity(5)
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .productId(testProduct2Id)
                    .quantity(5)
                    .build();

            Order order = Order.builder()
                    .items(List.of(item1, item2))
                    .build();

            when(productRepository.findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id)))
                    .thenReturn(List.of(testProduct1, testProduct2));

            // When
            inventoryService.restoreStock(order);

            // Then
            assertEquals(100, testProduct1.getStock());
            assertEquals(10, testProduct2.getStock());

            verify(productRepository).findAllByIdForUpdate(List.of(testProduct1Id, testProduct2Id));
        }

        @Test
        @DisplayName("Should ignore missing products when restoring stock")
        void shouldIgnoreMissingProductsWhenRestoringStock() {
            // Given
            UUID missingProductId = UUID.randomUUID();

            testProduct1.setStock(95);

            OrderItem existingItem = OrderItem.builder()
                    .productId(testProduct1Id)
                    .quantity(5)
                    .build();

            OrderItem missingItem = OrderItem.builder()
                    .productId(missingProductId)
                    .quantity(10)
                    .build();

            Order order = Order.builder()
                    .items(List.of(existingItem, missingItem))
                    .build();

            when(productRepository.findAllByIdForUpdate(List.of(testProduct1Id, missingProductId)))
                    .thenReturn(List.of(testProduct1));

            // When
            inventoryService.restoreStock(order);

            // Then
            assertEquals(100, testProduct1.getStock());

            verify(productRepository).findAllByIdForUpdate(List.of(testProduct1Id, missingProductId));
        }

        @Test
        @DisplayName("Should do nothing when all products are missing")
        void shouldDoNothingWhenAllProductsAreMissing() {
            // Given
            UUID missingProductId1 = UUID.randomUUID();
            UUID missingProductId2 = UUID.randomUUID();

            OrderItem item1 = OrderItem.builder()
                    .productId(missingProductId1)
                    .quantity(5)
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .productId(missingProductId2)
                    .quantity(10)
                    .build();

            Order order = Order.builder()
                    .items(List.of(item1, item2))
                    .build();

            when(productRepository.findAllByIdForUpdate(List.of(missingProductId1, missingProductId2)))
                    .thenReturn(List.of());

            // When
            inventoryService.restoreStock(order);

            // Then
            verify(productRepository).findAllByIdForUpdate(List.of(missingProductId1, missingProductId2));
        }
    }
}