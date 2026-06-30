package com.khaled.shopsphere.product;

import com.khaled.shopsphere.product.response.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ProductImageServices {
    CompletableFuture<ImageUploadResponse> upload(MultipartFile file);

    ImageUploadResponse upload(byte[] bytes, String folder);

}
