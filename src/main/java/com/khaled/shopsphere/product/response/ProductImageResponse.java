package com.khaled.shopsphere.product.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    private String url;
    private boolean primary;
}
