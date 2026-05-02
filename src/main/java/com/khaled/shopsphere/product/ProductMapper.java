package com.khaled.shopsphere.product;

import com.khaled.shopsphere.product.response.ProductImageResponse;
import com.khaled.shopsphere.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductMapper {
    public ProductResponse toProductResponse(Product product) {
        List<ProductImageResponse> images = product.getImages() == null
                ? List.of()
                : product.getImages()
                  .stream()
                  .map(img -> ProductImageResponse.builder()
                              .url(img.getUrl())
                              .primary(img.isPrimary())
                              .build())
                  .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .images(images)
                .build();
    }
}
