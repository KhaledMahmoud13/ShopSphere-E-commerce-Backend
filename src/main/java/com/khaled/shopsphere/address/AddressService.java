package com.khaled.shopsphere.address;

import com.khaled.shopsphere.address.request.CreateAddressRequest;
import com.khaled.shopsphere.address.request.UpdateAddressRequest;
import com.khaled.shopsphere.address.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    AddressResponse create(UUID userId, CreateAddressRequest request);

    List<AddressResponse> getAddresses(UUID userId);

    AddressResponse getById(UUID userId, UUID addressId);

    AddressResponse update(UUID userId, UUID addressId, UpdateAddressRequest request);

    void delete(UUID userId, UUID addressId);

    void setDefault(UUID userId, UUID addressId);
}
