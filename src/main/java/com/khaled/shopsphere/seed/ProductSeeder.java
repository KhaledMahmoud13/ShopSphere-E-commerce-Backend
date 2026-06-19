package com.khaled.shopsphere.seed;

import com.khaled.shopsphere.category.Category;
import com.khaled.shopsphere.category.CategoryRepository;
import com.khaled.shopsphere.product.Product;
import com.khaled.shopsphere.product.ProductImage;
import com.khaled.shopsphere.product.ProductImageServices;
import com.khaled.shopsphere.product.ProductRepository;
import com.khaled.shopsphere.product.response.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageServices productImageServices;

    private static final UUID SYSTEM_USER =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public void run(String... args) throws Exception {

        if (productRepository.count() > 0) {
            log.info("Products already exist. Skipping product seeding.");
            return;
        }

        Category electronics = getOrCreateCategory("Electronics");
        Category accessories = getOrCreateCategory("Accessories");
        Category gaming = getOrCreateCategory("Gaming");
        Category laptops = getOrCreateCategory("Laptops");

        List<ProductSeedData> products = List.of(
                new ProductSeedData(
                        "iPhone 17",
                        "Apple iPhone 17 Mist Blue 256GB",
                        new BigDecimal("829.00"),
                        50,
                        "seed/products/iphone-17.png",
                        electronics
                ),
                new ProductSeedData(
                        "Samsung Galaxy S25",
                        "Samsung flagship smartphone",
                        new BigDecimal("1199.99"),
                        40,
                        "seed/products/galaxy-s25.png",
                        electronics
                ),
                new ProductSeedData(
                        "MacBook Pro M4",
                        "Apple MacBook Pro M4 14-inch",
                        new BigDecimal("2499.99"),
                        20,
                        "seed/products/macbook-m4.png",
                        laptops
                ),
                new ProductSeedData(
                        "AirPods Pro 3",
                        "Apple premium wireless earbuds",
                        new BigDecimal("249.99"),
                        100,
                        "seed/products/airpods-pro-3.png",
                        accessories
                ),
                new ProductSeedData(
                        "Sony WH-1000XM5",
                        "Premium noise cancelling headphones",
                        new BigDecimal("399.99"),
                        35,
                        "seed/products/sony-wh1000xm5.png",
                        accessories
                ),
                new ProductSeedData(
                        "PlayStation 5",
                        "Sony PlayStation 5 Console",
                        new BigDecimal("699.99"),
                        15,
                        "seed/products/ps5.png",
                        gaming
                ),
                new ProductSeedData(
                        "iPad Air",
                        "Apple iPad Air 128GB",
                        new BigDecimal("799.99"),
                        25,
                        "seed/products/ipad-air.png",
                        electronics
                ),
                new ProductSeedData(
                        "Logitech MX Master 3S",
                        "Wireless productivity mouse",
                        new BigDecimal("129.99"),
                        60,
                        "seed/products/mx-master-3s.png",
                        accessories
                )
        );

        for (ProductSeedData data : products) {
            seedProduct(data);
        }

        log.info("Product seeding completed.");
    }

    private Category getOrCreateCategory(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name(name)
                                .createdBy(SYSTEM_USER)
                                .build()
                ));
    }

    private void seedProduct(ProductSeedData data) throws Exception {

        ClassPathResource resource = new ClassPathResource(data.imagePath());
        byte[] imageBytes = resource.getInputStream().readAllBytes();

        ImageUploadResponse uploadResponse =
                productImageServices.upload(imageBytes, "products");

        Product product = Product.builder()
                .name(data.name())
                .description(data.description())
                .price(data.price())
                .stock(data.stock())
                .category(data.category())
                .createdBy(SYSTEM_USER)
                .build();

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(uploadResponse.getUrl())
                .publicId(uploadResponse.getPublicId())
                .primary(true)
                .createdBy(SYSTEM_USER)
                .build();

        product.setImages(Set.of(image));

        productRepository.save(product);

        log.info("Seeded product: {}", product.getName());
    }

    private record ProductSeedData(
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String imagePath,
            Category category
    ) {
    }
}