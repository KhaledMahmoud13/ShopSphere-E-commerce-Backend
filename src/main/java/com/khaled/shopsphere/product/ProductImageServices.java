package com.khaled.shopsphere.product;

import com.khaled.shopsphere.product.response.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ProductImageServices {
    public CompletableFuture<ImageUploadResponse> upload(MultipartFile file);
}
