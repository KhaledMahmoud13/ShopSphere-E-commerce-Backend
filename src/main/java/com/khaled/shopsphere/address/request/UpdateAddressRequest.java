package com.khaled.shopsphere.address.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressRequest {
    @NotBlank
    private String recipientName;

    @NotBlank
    private String recipientPhone;

    @NotBlank
    private String country;

    @NotBlank
    private String city;

    @NotBlank
    private String area;

    @NotBlank
    private String street;

    @NotBlank
    private String buildingNumber;

    private String floorNumber;

    private String apartmentNumber;

    private String postalCode;

    private boolean isDefault;
}
