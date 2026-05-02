package com.khaled.shopsphere.permission;

import com.khaled.shopsphere.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PERMISSIONS")
public class Permission extends BaseEntity {
    @Column(name = "NAME", unique = true, nullable = false)
    private String name;
}
