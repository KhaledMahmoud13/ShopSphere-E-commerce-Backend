package com.khaled.shopsphere.address.impl;

import com.khaled.shopsphere.address.Address;
import com.khaled.shopsphere.address.AddressMapper;
import com.khaled.shopsphere.address.AddressRepository;
import com.khaled.shopsphere.address.AddressService;
import com.khaled.shopsphere.address.request.CreateAddressRequest;
import com.khaled.shopsphere.address.request.UpdateAddressRequest;
import com.khaled.shopsphere.address.response.AddressResponse;
import com.khaled.shopsphere.exception.BusinessException;
import com.khaled.shopsphere.user.User;
import com.khaled.shopsphere.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.ADDRESS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {

        if (request.isDefault()) {
            clearDefaultAddresses(user.getId());
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

        if (request.getIsDefault() != null) {
            if (request.getIsDefault()) {
                clearDefaultAddresses(userId);
            }
            address.setIsDefault(request.getIsDefault());
        }

        address.setRecipientName(valueOrCurrent(normalize(request.getRecipientName()), address.getRecipientName()));
        address.setRecipientPhone(valueOrCurrent(normalize(request.getRecipientPhone()), address.getRecipientPhone()));
        address.setCountry(valueOrCurrent(normalize(request.getCountry()), address.getCountry()));
        address.setCity(valueOrCurrent(normalize(request.getCity()), address.getCity()));
        address.setArea(valueOrCurrent(normalize(request.getArea()), address.getArea()));
        address.setStreet(valueOrCurrent(normalize(request.getStreet()), address.getStreet()));
        address.setBuildingNumber(valueOrCurrent(normalize(request.getBuildingNumber()), address.getBuildingNumber()));
        address.setFloorNumber(valueOrCurrent(normalize(request.getFloorNumber()), address.getFloorNumber()));
        address.setApartmentNumber(valueOrCurrent(normalize(request.getApartmentNumber()), address.getApartmentNumber()));
        address.setPostalCode(valueOrCurrent(normalize(request.getPostalCode()), address.getPostalCode()));

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

        address.setIsDefault(true);
    }

    private void clearDefaultAddresses(UUID userId) {
        addressRepository.clearDefaultAddress(userId);
    }

    private <T> T valueOrCurrent(T newValue, T currentValue) {
        return newValue != null ? newValue : currentValue;
    }

    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
