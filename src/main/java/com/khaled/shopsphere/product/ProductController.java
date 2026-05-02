package com.khaled.shopsphere.product;

import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.product.request.CreateProductRequest;
import com.khaled.shopsphere.product.request.ProductFilterRequest;
import com.khaled.shopsphere.product.response.ProductResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API")
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin:access')")
    public ProductResponse create(
            @Parameter(
                    description = "Product data",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @Valid @RequestPart("data") CreateProductRequest request,
            @Parameter(
                    description = "Product images",
                    required = true
            )
            @RequestPart("images") List<MultipartFile> images
    ) {
        return productService.create(request, images);
    }

    @GetMapping
    public PageResponse<ProductResponse> getProducts(
            @Valid ProductFilterRequest filter,
            Pageable pageable
    ) {
        return productService.getProducts(filter, pageable);
    }
}
