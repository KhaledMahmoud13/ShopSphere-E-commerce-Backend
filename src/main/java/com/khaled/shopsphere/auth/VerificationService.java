package com.khaled.shopsphere.auth;

import com.khaled.shopsphere.auth.request.SendCodeRequest;
import com.khaled.shopsphere.auth.request.VerifyEmailRequest;

public interface VerificationService {
    void sendVerificationCode(SendCodeRequest request);

    boolean verifyCode(VerifyEmailRequest request);
}
