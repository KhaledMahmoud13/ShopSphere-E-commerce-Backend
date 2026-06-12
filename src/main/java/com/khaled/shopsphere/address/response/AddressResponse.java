package com.khaled.shopsphere.address.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
    private UUID id;

    private String recipientName;

    private String recipientPhone;

    private String country;

    private String city;

    private String area;

    private String street;

    private String buildingNumber;

    private String floorNumber;

    private String apartmentNumber;

    private String postalCode;

    private boolean isDefault;
}
