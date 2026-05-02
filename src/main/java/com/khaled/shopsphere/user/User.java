package com.khaled.shopsphere.user;

import com.khaled.shopsphere.common.BaseEntity;
import com.khaled.shopsphere.permission.Permission;
import com.khaled.shopsphere.role.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

import static jakarta.persistence.GenerationType.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "USERS")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = UUID)
    private UUID id;
    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;
    @Column(name = "PHONE_NUMBER", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    @Column(name = "IS_ENABLED")
    private boolean enabled;
    @Column(name = "IS_ACCOUNT_LOCKED")
    private boolean accountLocked;
    @Column(name = "IS_CREDENTIAL_EXPIRED")
    private boolean credentialsExpired;
    @Column(name = "IS_EMAIL_VERIFIED")
    private boolean emailVerified;
    @Column(name = "IS_PHONE_VERIFIED")
    private boolean phoneVerified;
    @CreatedDate
    @Column(name = "CREATED_DATE", updatable = false, nullable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE", insertable = false)
    private LocalDateTime lastModifiedDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : this.roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialsExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}