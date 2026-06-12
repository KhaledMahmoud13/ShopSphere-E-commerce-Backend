package com.khaled.shopsphere.auth.message;

public record VerificationEmailMessage(String email, String code) {
}
