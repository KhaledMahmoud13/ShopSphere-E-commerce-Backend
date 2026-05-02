package com.khaled.shopsphere.product;

import com.khaled.shopsphere.common.BaseEntity;
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
@Table(name = "PRODUCT_IMAGES")
public class ProductImage extends BaseEntity {
    @Column(name = "URL", nullable = false)
    private String url;
    @Column(name = "PUBLIC_ID", nullable = false)
    private String publicId;
    @Column(name = "IS_PRIMARY")
    private boolean primary;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;
}
