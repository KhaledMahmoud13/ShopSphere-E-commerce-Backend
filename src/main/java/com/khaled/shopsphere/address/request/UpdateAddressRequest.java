package com.khaled.shopsphere.address.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressRequest {
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
    private Boolean isDefault;
}
