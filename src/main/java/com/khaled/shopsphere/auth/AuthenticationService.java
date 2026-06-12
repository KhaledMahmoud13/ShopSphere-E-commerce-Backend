package com.khaled.shopsphere.auth;

import com.khaled.shopsphere.auth.request.*;
import com.khaled.shopsphere.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    void verifyEmail(VerifyEmailRequest request);

    void resendVerificationCode(SendCodeRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);
}