package com.khaled.shopsphere.auth.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshRequest {
    private String refreshToken;
}
