package com.khaled.shopsphere.auth.impl;

import com.khaled.shopsphere.auth.AuthenticationService;
import com.khaled.shopsphere.auth.request.AuthenticationRequest;
import com.khaled.shopsphere.auth.request.RefreshRequest;
import com.khaled.shopsphere.auth.request.RegistrationRequest;
import com.khaled.shopsphere.auth.response.AuthenticationResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.role.Role;
import com.khaled.shopsphere.role.RoleRepository;
import com.khaled.shopsphere.security.JwtService;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserMapper;
import com.khaled.shopsphere.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.khaled.shopsphere.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        final User user = (User) auth.getPrincipal();
        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        checkUserEmail(request.getEmail());
        checkUserPhoneNumber(request.getPhoneNumber());
        checkPasswords(request.getPassword(), request.getConfirmPassword());

        Role defaultRole = this.roleRepository.findByNameWithPermissions("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException(
                        "ROLE_USER not found"));

        final User user = this.userMapper.toUser(request);
        user.getRoles().add(defaultRole);

        log.debug("Saving user {}", user);
        this.userRepository.save(user);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest req) {
        final String newAccessToken = this.jwtService.refreshAccessToken(req.getRefreshToken());
        final String tokenType = "Bearer";
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(req.getRefreshToken())
                .tokenType(tokenType)
                .build();
    }

    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(PASSWORD_MISMATCH);
        }
    }

    private void checkUserPhoneNumber(final String phoneNumber) {
        final boolean phoneNumberExists = this.userRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExists) {
            throw new BusinessException(PHONE_ALREADY_EXISTS);
        }
    }
}
