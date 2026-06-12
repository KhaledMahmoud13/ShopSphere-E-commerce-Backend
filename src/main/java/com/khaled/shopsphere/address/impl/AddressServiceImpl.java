package com.khaled.shopsphere.address.impl;

import com.khaled.shopsphere.address.Address;
import com.khaled.shopsphere.address.AddressMapper;
import com.khaled.shopsphere.address.AddressRepository;
import com.khaled.shopsphere.address.AddressService;
import com.khaled.shopsphere.address.request.CreateAddressRequest;
import com.khaled.shopsphere.address.request.UpdateAddressRequest;
import com.khaled.shopsphere.address.response.AddressResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.exception.ErrorCode;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.ADDRESS_NOT_FOUND;
import static com.khaled.shopsphere.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse create(UUID userId, CreateAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (request.isDefault()) {
            clearDefaultAddresses(userId);
        }

        Address address = Address.builder()
                .user(user)
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .country(request.getCountry())
                .city(request.getCity())
                .area(request.getArea())
                .street(request.getStreet())
                .buildingNumber(request.getBuildingNumber())
                .floorNumber(request.getFloorNumber())
                .apartmentNumber(request.getApartmentNumber())
                .postalCode(request.getPostalCode())
                .isDefault(request.isDefault())
                .build();

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(UUID userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getById(UUID userId, UUID addressId) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new BusinessException(ADDRESS_NOT_FOUND)
                );

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse update(UUID userId, UUID addressId, UpdateAddressRequest request) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new BusinessException(ADDRESS_NOT_FOUND)
                );

        if (request.isDefault()) {
            clearDefaultAddresses(userId);
        }

        address.setRecipientName(request.getRecipientName());
        address.setRecipientPhone(request.getRecipientPhone());
        address.setCountry(request.getCountry());
        address.setCity(request.getCity());
        address.setArea(request.getArea());
        address.setStreet(request.getStreet());
        address.setBuildingNumber(request.getBuildingNumber());
        address.setFloorNumber(request.getFloorNumber());
        address.setApartmentNumber(request.getApartmentNumber());
        address.setPostalCode(request.getPostalCode());
        address.setDefault(request.isDefault());

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID addressId) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new BusinessException(ADDRESS_NOT_FOUND)
                );

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefault(UUID userId, UUID addressId) {
        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new BusinessException(ADDRESS_NOT_FOUND)
                );

        clearDefaultAddresses(userId);

        address.setDefault(true);
    }

    private void clearDefaultAddresses(UUID userId) {

        addressRepository.findByUserId(userId)
                .forEach(address ->
                        address.setDefault(false)
                );
    }
}
