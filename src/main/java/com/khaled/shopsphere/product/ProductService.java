package com.khaled.shopsphere.product;

import com.khaled.shopsphere.common.PageResponse;
import com.khaled.shopsphere.product.request.CreateProductRequest;
import com.khaled.shopsphere.product.request.ProductFilterRequest;
import com.khaled.shopsphere.product.response.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(CreateProductRequest request, List<MultipartFile> files);

    PageResponse<ProductResponse> getProducts(ProductFilterRequest filter, Pageable pageable);
}
