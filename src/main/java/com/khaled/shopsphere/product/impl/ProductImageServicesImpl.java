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
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServicesImpl implements ProductImageServices {
    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    private static final Set<String> ALLOWED_SIGNATURES = Set.of(
            "FFD8FF",   // JPEG
            "89504E47", // PNG
            "524946"    // WebP (RIFF)
    );

    @Async("imageUploadExecutor")
    @Override
    public CompletableFuture<ImageUploadResponse> upload(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            validateSize(bytes.length);
            validateType(bytes);

            Map<?, ?> result = cloudinary.uploader()
                    .upload(bytes, ObjectUtils.asMap(
                            "folder", "products",
                            "resource_type", "image",
                            "format", "webp"
                    ));

            ImageUploadResponse response = ImageUploadResponse.builder()
                    .url(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();

            return CompletableFuture.completedFuture(response);
        } catch (BusinessException e) {
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            log.error("Cloudinary upload failed for file: {}", file.getOriginalFilename(), e);
            return CompletableFuture.failedFuture(new BusinessException(IMAGE_UPLOAD_FAILED));
        }
    }

    private void validateSize(long sizeInBytes) {
        if (sizeInBytes == 0) {
            throw new BusinessException(INVALID_IMAGE);
        }
        if (sizeInBytes > MAX_FILE_SIZE) {
            throw new BusinessException(FILE_TOO_LARGE);
        }
    }

    private void validateType(byte[] bytes) {
        if (bytes.length < 4) {
            throw new BusinessException(UNSUPPORTED_FILE_TYPE);
        }

        String hex = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
        log.info("Hex: {}", hex);
        boolean valid = ALLOWED_SIGNATURES.stream().anyMatch(hex::startsWith);
        if (!valid) throw new BusinessException(UNSUPPORTED_FILE_TYPE);
    }
}
