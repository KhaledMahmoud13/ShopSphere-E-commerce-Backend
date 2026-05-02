package com.khaled.shopsphere.user;

import com.khaled.shopsphere.auth.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(final RegistrationRequest request) {
        return User.builder()
                   .firstName(request.getFirstName())
                   .lastName(request.getLastName())
                   .email(request.getEmail())
                   .phoneNumber(request.getPhoneNumber())
                   .password(this.passwordEncoder.encode(request.getPassword()))
                   .enabled(true)
                   .accountLocked(false)
                   .credentialsExpired(false)
                   .emailVerified(false)
                   .phoneVerified(false)
                   .build();
    }
}
