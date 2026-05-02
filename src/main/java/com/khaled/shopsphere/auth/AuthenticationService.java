package com.khaled.shopsphere.auth;

import com.khaled.shopsphere.auth.request.AuthenticationRequest;
import com.khaled.shopsphere.auth.request.RefreshRequest;
import com.khaled.shopsphere.auth.request.RegistrationRequest;
import com.khaled.shopsphere.auth.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    AuthenticationResponse refreshToken(RefreshRequest req);
}