package com.khaled.shopsphere.address;

import com.khaled.shopsphere.common.BaseEntity;
import com.khaled.shopsphere.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "ADDRESSES")
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "RECIPIENT_NAME", nullable = false)
    private String recipientName;

    @Column(name = "RECIPIENT_PHONE", nullable = false)
    private String recipientPhone;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Column(name = "CITY", nullable = false)
    private String city;

    @Column(name = "AREA", nullable = false)
    private String area;

    @Column(name = "STREET", nullable = false)
    private String street;

    @Column(name = "BUILDING_NUMBER", nullable = false)
    private String buildingNumber;

    @Column(name = "FLOOR_NUMBER")
    private String floorNumber;

    @Column(name = "APARTMENT_NUMBER")
    private String apartmentNumber;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;
}
