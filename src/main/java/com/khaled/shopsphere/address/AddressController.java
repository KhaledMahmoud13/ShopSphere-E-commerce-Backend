package com.khaled.shopsphere.address;

import com.khaled.shopsphere.address.request.CreateAddressRequest;
import com.khaled.shopsphere.address.request.UpdateAddressRequest;
import com.khaled.shopsphere.address.response.AddressResponse;
import com.khaled.shopsphere.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
@Tag(name = "Address", description = "Address API")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasAuthority('address:write')")
    public ResponseEntity<AddressResponse> create(
            @Valid @RequestBody CreateAddressRequest request,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(addressService.create(userId, request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('address:read')")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasAuthority('address:read')")
    public ResponseEntity<AddressResponse> getById(
            @PathVariable UUID addressId,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(addressService.getById(userId, addressId));
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasAuthority('address:write')")
    public ResponseEntity<AddressResponse> update(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest request,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        return ResponseEntity.ok(addressService.update(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasAuthority('address:write')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID addressId,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        addressService.delete(userId, addressId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    @PreAuthorize("hasAuthority('address:write')")
    public ResponseEntity<Void> setDefault(
            @PathVariable UUID addressId,
            Authentication authentication
    ) {
        UUID userId = ((User) authentication.getPrincipal()).getId();

        addressService.setDefault(userId, addressId);

        return ResponseEntity.noContent().build();
    }
}
