package com.khaled.shopsphere.cart.impl;

import com.khaled.shopsphere.cart.Cart;
import com.khaled.shopsphere.cart.CartItem;
import com.khaled.shopsphere.cart.CartRepository;
import com.khaled.shopsphere.cart.request.AddCartItemRequest;
import com.khaled.shopsphere.cart.request.UpdateCartItemRequest;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductRepository;
import com.khaled.shopsphere.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CartServiceImpl Unit Tests")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID testUserId;
    private UUID testProductId;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private AddCartItemRequest addCartItemRequest;
    private UpdateCartItemRequest updateCartItemRequest;

    @BeforeEach
    void setup() {
        this.testUserId = UUID.randomUUID();
        this.testProductId = UUID.randomUUID();

        this.testUser = User.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .emailVerified(true)
                .build();

        this.testProduct = Product.builder()
                .id(testProductId)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(10.99))
                .stock(10)
                .build();

        this.testCart = Cart.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .items(new ArrayList<>())
                .build();

        this.addCartItemRequest = AddCartItemRequest.builder()
                .productId(testProductId)
                .quantity(1)
                .build();

        this.updateCartItemRequest = UpdateCartItemRequest.builder().quantity(10).build();
    }

    @Nested
    @DisplayName("Add Item To Cart Tests")
    class AddItemToCartTests {

        @Test
        @DisplayName("Should increase quantity when product already exists in cart")
        void shouldIncreaseQuantityWhenProductAlreadyExistsInCart() {
            // Given
            int oldQuantity = 2;
            CartItem existingItem = CartItem.builder()
                    .product(testProduct)
                    .quantity(oldQuantity)
                    .build();
            testCart.setItems(new ArrayList<>(List.of(existingItem)));

            when(productRepository.findById(addCartItemRequest.getProductId())).thenReturn(Optional.of(testProduct));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When
            cartService.addItem(addCartItemRequest, testUser);

            // Then
            assertEquals(oldQuantity + addCartItemRequest.getQuantity(), existingItem.getQuantity());
            assertEquals(1, testCart.getItems().size());

            verify(cartRepository).save(testCart);
        }

        @Test
        @DisplayName("Should add new item when product does not exist in cart")
        void shouldAddNewItemWhenProductDoesNotExistInCart() {
            // Given
            Product anotherProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .name("Another Product")
                    .build();
            CartItem existingItem = CartItem.builder()
                    .product(anotherProduct)
                    .quantity(1)
                    .build();
            testCart.setItems(new ArrayList<>(List.of(existingItem)));

            when(productRepository.findById(addCartItemRequest.getProductId())).thenReturn(Optional.of(testProduct));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When
            cartService.addItem(addCartItemRequest, testUser);

            // Then
            assertEquals(2, testCart.getItems().size());

            CartItem addedItem = testCart.getItems()
                    .stream()
                    .filter(item -> item.getProduct().getId().equals(testProductId))
                    .findFirst()
                    .orElseThrow();

            assertEquals(testProduct, addedItem.getProduct());
            assertEquals(1, addedItem.getQuantity());

            verify(cartRepository).save(testCart);
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            when(productRepository.findById(addCartItemRequest.getProductId())).thenReturn(Optional.empty());

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> cartService.addItem(addCartItemRequest, testUser)
            );

            assertEquals("Product not found", exception.getMessage());
            verify(productRepository).findById(addCartItemRequest.getProductId());

            verifyNoInteractions(cartRepository);
        }

        @Test
        @DisplayName("Should create cart when cart not found")
        void shouldCreateCartWhenCartNotFound() {
            // Given
            when(productRepository.findById(addCartItemRequest.getProductId())).thenReturn(Optional.of(testProduct));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            cartService.addItem(addCartItemRequest, testUser);

            // Then
            verify(cartRepository, times(2)).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Update Quantity Tests")
    class UpdateQuantityTests {

        @Test
        @DisplayName("Should update quantity when cart exists and contains the product")
        void shouldUpdateQuantityWhenCartExistsAndContainsTheProduct() {
            // Given
            CartItem existingItem = CartItem.builder()
                    .product(testProduct)
                    .quantity(2)
                    .build();
            testCart.setItems(new ArrayList<>(List.of(existingItem)));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When
            cartService.updateQuantity(testProductId, updateCartItemRequest, testUserId);

            // Then
            assertEquals(updateCartItemRequest.getQuantity(), existingItem.getQuantity());
            verify(cartRepository).findByUserId(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> cartService.updateQuantity(testProductId, updateCartItemRequest, testUserId)
            );

            assertEquals("Cart not found for user with id " + testUserId, exception.getMessage());
            verify(cartRepository).findByUserId(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when product not found in cart")
        void shouldThrowExceptionWhenProductNotFoundInCart() {
            // Given
            Product anotherProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .name("Another Product")
                    .build();

            CartItem item = CartItem.builder()
                    .product(anotherProduct)
                    .quantity(2)
                    .build();

            testCart.setItems(new ArrayList<>(List.of(item)));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> cartService.updateQuantity(testProductId, updateCartItemRequest, testUserId)
            );

            assertEquals("Cart item not found", exception.getMessage());
            verify(cartRepository).findByUserId(testUserId);
        }
    }

    @Nested
    @DisplayName("Remove Item Tests")
    class RemoveItemTests {

        @Test
        @DisplayName("Should remove item when cart exists and contains the product")
        void shouldRemoveItemWhenCartExistsAndContainsTheProduct() {
            // Given
            CartItem existingItem = CartItem.builder()
                    .product(testProduct)
                    .quantity(20)
                    .build();
            testCart.setItems(new ArrayList<>(List.of(existingItem)));
            int cartSize = testCart.getItems().size();
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When
            cartService.removeItem(testProductId, testUserId);

            // Then
            assertEquals(cartSize - 1, testCart.getItems().size());
            verify(cartRepository).findByUserId(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.empty());

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> cartService.removeItem(testProductId, testUserId)
            );

            assertEquals("Cart not found for user with id " + testUserId, exception.getMessage());
            verify(cartRepository).findByUserId(testUserId);
        }

        @Test
        @DisplayName("Should throw exception when product not found in cart")
        void shouldThrowsExceptionWhenProductNotFoundInCart() {
            // Given
            Product anotherProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .name("Another Product")
                    .build();

            CartItem item = CartItem.builder()
                    .product(anotherProduct)
                    .quantity(2)
                    .build();

            testCart.setItems(new ArrayList<>(List.of(item)));
            when(cartRepository.findByUserId(testUserId)).thenReturn(Optional.of(testCart));

            // When & Then
            final BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> cartService.removeItem(testProductId, testUserId)
            );

            assertEquals("Cart item not found", exception.getMessage());
            verify(cartRepository).findByUserId(testUserId);
        }
    }
}