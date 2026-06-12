package com.khaled.shopsphere.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyEmailRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String code;
}
