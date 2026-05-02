package com.khaled.shopsphere.product.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.product.ProductImageServices;
import com.khaled.shopsphere.product.response.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServicesImpl implements ProductImageServices {
    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    @Async
    @Override
    public CompletableFuture<ImageUploadResponse> upload(MultipartFile file) {

        validate(file);

        try {
            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), ObjectUtils.emptyMap());

            ImageUploadResponse response =  ImageUploadResponse.builder()
                    .url(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();

            return CompletableFuture.completedFuture(response);
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new BusinessException(IMAGE_UPLOAD_FAILED);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(INVALID_IMAGE);
        }

        validateSize(file);
        validateType(file);
    }

    private void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(FILE_TOO_LARGE);
        }
    }

    private void validateType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(UNSUPPORTED_FILE_TYPE);
        }
    }
}
