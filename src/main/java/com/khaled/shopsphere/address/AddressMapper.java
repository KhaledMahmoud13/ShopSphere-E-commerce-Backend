package com.khaled.shopsphere.address;

import com.khaled.shopsphere.address.response.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressMapper {
    public AddressResponse toResponse(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .country(address.getCountry())
                .city(address.getCity())
                .area(address.getArea())
                .street(address.getStreet())
                .buildingNumber(address.getBuildingNumber())
                .floorNumber(address.getFloorNumber())
                .apartmentNumber(address.getApartmentNumber())
                .postalCode(address.getPostalCode())
                .isDefault(address.isDefault())
                .build();
    }
}
