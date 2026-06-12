package com.khaled.shopsphere.auth.event;

public record VerificationEmailEvent(String email, String code) {
}
