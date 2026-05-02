package com.khaled.shopsphere.product;

import com.khaled.shopsphere.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "PRODUCTS")
public class Product extends BaseEntity {
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;
    @Column(name = "STOCK", nullable = false)
    private Integer stock;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
}
