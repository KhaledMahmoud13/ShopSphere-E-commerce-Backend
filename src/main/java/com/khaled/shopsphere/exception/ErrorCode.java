package com.khaled.shopsphere.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {


    INVALID_TOKEN("INVALID_TOKEN", "Invalid token", UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token expired", UNAUTHORIZED),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", "Invalid token type", UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", "Email already exists", CONFLICT),
    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", "An account with this phone number already exists", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "The password and confirmation do not match", BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", NOT_FOUND),
    CHANGE_PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "New password and confirmation do not match", BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "The current password is incorrect", BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account has been deactivated", BAD_REQUEST),
    ERR_USER_DISABLED("ERR_USER_DISABLED", "User account is disabled, please activate your account or contact the administrator", UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided username", NOT_FOUND),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "An internal exception occurred, please try again or contact the admin", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_UPLOAD_FAILED("IMAGE_UPLOAD_FAILED", "Failed to upload image", INTERNAL_SERVER_ERROR),
    INVALID_IMAGE("INVALID_IMAGE", "Invalid or empty image file", BAD_REQUEST),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "File size exceeds allowed limit", BAD_REQUEST),
    UNSUPPORTED_FILE_TYPE("UNSUPPORTED_FILE_TYPE", "Unsupported file type", BAD_REQUEST),
    PRODUCT_CREATION_FAILED("PRODUCT_CREATION_FAILED", "Failed to create product", INTERNAL_SERVER_ERROR),
    IMAGES_REQUIRED("IMAGES_REQUIRED", "At least one image is required", BAD_REQUEST),
    MAX_IMAGES_EXCEEDED("MAX_IMAGES_EXCEEDED", "Maximum %s images allowed", BAD_REQUEST),
    AUTH_REQUIRED("UNAUTHORIZED", "Authentication is required to access this resource", HttpStatus.UNAUTHORIZED),
    ORDER_HAS_NO_ITEMS("ORDER_HAS_NO_ITEMS", "Order must contain at least one item", BAD_REQUEST),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order not found", NOT_FOUND),
    UNAUTHORIZED_ORDER_ACCESS("UNAUTHORIZED_ORDER_ACCESS", "You are not authorized to access this order", HttpStatus.UNAUTHORIZED),
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found", NOT_FOUND),
    ITEM_OUT_OF_STOCK("ITEM_OUT_OF_STOCK", "Product '%s' is out of stock", BAD_REQUEST),
    INVALID_ITEM_QUANTITY("INVALID_ITEM_QUANTITY", "Item quantity must be greater than 0", BAD_REQUEST),
    CART_NOT_FOUND("CART_NOT_FOUND", "Cart not found for user with id %s", NOT_FOUND),
    CART_IS_EMPTY("CART_IS_EMPTY", "Cart is empty", BAD_REQUEST),
    ORDER_ALREADY_CANCELLED("ORDER_ALREADY_CANCELLED", "Order is already cancelled", BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELLED("ORDER_CANNOT_BE_CANCELLED", "Order cannot be cancelled in status %s", BAD_REQUEST),
    INVALID_ORDER_STATUS_TRANSITION("INVALID_ORDER_STATUS_TRANSITION", "Cannot change order status from %s to %s", BAD_REQUEST),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found", NOT_FOUND),
    PAYMENT_ALREADY_EXISTS("PAYMENT_ALREADY_EXISTS", "Payment already exists", CONFLICT),
    ADDRESS_NOT_FOUND("ADDRESS_NOT_FOUND", "Address not found", NOT_FOUND),
    INVALID_VERIFICATION_CODE("INVALID_VERIFICATION_CODE", "Invalid verification code", BAD_REQUEST),
    ACCOUNT_ALREADY_EMAIL_VERIFIED("ACCOUNT_ALREADY_EMAIL_VERIFIED", "Email is already verified", BAD_REQUEST),
    STRIPE_SESSION_CREATION_FAILED("STRIPE_SESSION_CREATION_FAILED", "Failed to create Stripe session", INTERNAL_SERVER_ERROR),
    PAYMENT_ALREADY_SUCCEEDED("PAYMENT_ALREADY_SUCCEEDED", "Payment has already been succeeded", BAD_REQUEST),
    INVALID_STRIPE_SIGNATURE("INVALID_STRIPE_SIGNATURE", "Invalid Stripe signature", BAD_REQUEST),
    STRIPE_EVENT_DESERIALIZATION_FAILED("STRIPE_EVENT_DESERIALIZATION_FAILED", "Failed to deserialize Stripe event", INTERNAL_SERVER_ERROR),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Category not found", NOT_FOUND),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(final String code,
              final String defaultMessage,
              final HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
