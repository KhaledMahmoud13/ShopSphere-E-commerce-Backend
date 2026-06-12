package com.khaled.shopsphere.email;

public interface EmailService {
    void sendVerificationEmail(String email, String code);
}
