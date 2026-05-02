package com.khaled.shopsphere.common;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.GenerationType.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE", insertable = false)
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false, nullable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY", insertable = false)
    private UUID lastModifiedBy;
}