package com.khaled.shopsphere.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.product.*;
import com.khaled.shopsphere.product.request.CreateProductRequest;
import com.khaled.shopsphere.product.request.ProductFilterRequest;
import com.khaled.shopsphere.product.response.ImageUploadResponse;
import com.khaled.shopsphere.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductImageServices productImageServices;
    private final Cloudinary cloudinary;
    private final ProductMapper productMapper;

    private static final int MAX_IMAGES = 5;


    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("name", "price", "stock", "createdAt");

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request, List<MultipartFile> files) {
        validate(files);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        List<String> uploadedPublicIds = new ArrayList<>();

        try {
            List<CompletableFuture<ImageUploadResponse>> futures = files.stream()
                    .map(productImageServices::upload)
                    .toList();

            List<ImageUploadResponse> uploads = futures.stream()
                    .map(future -> {
                        try {
                            return future.join();
                        } catch (Exception e) {
                            throw new BusinessException(IMAGE_UPLOAD_FAILED);
                        }
                    })
                    .toList();

            List<ProductImage> images = new ArrayList<>();

            for (int i = 0; i < uploads.size(); i++) {
                ImageUploadResponse upload = uploads.get(i);

                uploadedPublicIds.add(upload.getPublicId());

                images.add(ProductImage.builder()
                        .url(upload.getUrl())
                        .publicId(upload.getPublicId())
                        .primary(i == 0)
                        .product(product)
                        .build());
            }

            product.setImages(images);

            Product savedProduct = repository.save(product);

            return productMapper.toProductResponse(savedProduct);

        } catch (BusinessException e) {
            cleanupUploadedImages(uploadedPublicIds);
            throw e;
        } catch (Exception e) {
            cleanupUploadedImages(uploadedPublicIds);
            log.error("Error creating product", e);
            throw new BusinessException(PRODUCT_CREATION_FAILED);
        }
    }

    @Override
    public PageResponse<ProductResponse> getProducts(ProductFilterRequest filter, Pageable pageable) {
        var spec = ProductSpecification.withFilters(
                filter.getSearch(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getMinStock(),
                filter.getMaxStock()
        );

        Pageable finalPageable = pageable;

        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {

            Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            List<Sort.Order> orders = filter.getSortBy().stream()
                    .filter(ALLOWED_SORT_FIELDS::contains)
                    .map(field -> new Sort.Order(direction, field))
                    .toList();

            if (!orders.isEmpty()) {
                finalPageable = PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(orders)
                );
            }
        }

        var page = repository.findAll(spec, finalPageable)
                .map(productMapper::toProductResponse);

        return PageResponse.<ProductResponse>builder()
                .data(page.getContent())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(IMAGES_REQUIRED);
        }

        if (files.size() > MAX_IMAGES) {
            throw new BusinessException(MAX_IMAGES_EXCEEDED, MAX_IMAGES);
        }
    }

    private void cleanupUploadedImages(List<String> publicIds) {
        for (String publicId : publicIds) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception ignored) {
            }
        }
    }
}
