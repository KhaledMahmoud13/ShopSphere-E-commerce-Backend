package com.khaled.shopsphere.order.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAddressResponse {
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
}
