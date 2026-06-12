package com.khaled.shopsphere.auth.request;

import com.khaled.shopsphere.validation.NonDisposableEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank(message = "VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK")
    @Size(min = 1, max = 50, message = "VALIDATION.REGISTRATION.FIRSTNAME.SIZE")
    @Pattern(regexp = "^[\\p{L} '-]+$", message = "VALIDATION.REGISTRATION.FIRST_NAME.PATTERN")
    @Schema(example = "Khaled")
    private String firstName;

    @NotBlank(message = "VALIDATION.REGISTRATION.LAST_NAME.BLANK")
    @Size(min = 1, max = 50, message = "VALIDATION.REGISTRATION.LAST_NAME.SIZE")
    @Pattern(regexp = "^[\\p{L} '-]+$", message = "VALIDATION.REGISTRATION.LAST_NAME.PATTERN")
    @Schema(example = "Mahmoud")
    private String lastName;

    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.BLANK")
    @Email(message = "VALIDATION.REGISTRATION.EMAIL.FORMAT")
    @NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")
    @Schema(example = "khaled@mail.com")
    private String email;

    @NotBlank(message = "VALIDATION.REGISTRATION.PHONE.BLANK")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "VALIDATION.REGISTRATION.PHONE.FORMAT")
    @Schema(example = "+201234567891")
    private String phoneNumber;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.BLANK")
    @Size(min = 8, max = 72, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$", message = "VALIDATION.REGISTRATION.PASSWORD.WEAK")
    @Schema(example = "password1")
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.BLANK")
    @Size(min = 8, max = 72, message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.SIZE")
    @Schema(example = "password1")
    private String confirmPassword;
}
