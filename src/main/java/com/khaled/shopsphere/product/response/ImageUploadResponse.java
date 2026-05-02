package com.khaled.shopsphere.product.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadResponse {
    private String url;
    private String publicId;
}
