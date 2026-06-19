package com.khaled.shopsphere.category;

import com.khaled.shopsphere.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "CATEGORIES")
public class Category extends BaseEntity {
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
}
