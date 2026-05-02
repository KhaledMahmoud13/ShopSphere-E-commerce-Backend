package com.khaled.shopsphere.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

    @Query("""
        SELECT DISTINCT r
        FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.name = :name
    """)
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    @Query("""
        SELECT DISTINCT r
        FROM Role r
        LEFT JOIN FETCH r.permissions
        WHERE r.name IN :names
    """)
    Set<Role> findByNamesWithPermissions(@Param("names") Set<String> names);
}