package com.khaled.shopsphere.product;

import com.khaled.shopsphere.category.Category;
import com.khaled.shopsphere.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Formula("(SELECT pi.url FROM PRODUCT_IMAGES pi WHERE pi.product_id = id AND pi.is_primary = true LIMIT 1)")
    private String primaryImageUrl;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImage> images = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
}
