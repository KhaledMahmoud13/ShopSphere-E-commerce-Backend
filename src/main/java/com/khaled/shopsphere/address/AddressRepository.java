package com.khaled.shopsphere.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByUserId(UUID userId);

    Optional<Address> findByIdAndUserId(UUID addressId, UUID userId);

    @Modifying
    @Query("""
                update Address a
                set a.isDefault = false
                where a.user.id = :userId
                  and a.isDefault = true
            """)
    int clearDefaultAddress(UUID userId);

    Optional<Address> findByUserIdAndIsDefaultTrue(UUID userId);
}
