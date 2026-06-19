package com.khaled.shopsphere.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecification {
    public static Specification<Product> withFilters(
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock,
            UUID categoryId
    ) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (search != null && !search.isBlank()) {
                var searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
                );
                predicate = cb.and(predicate, searchPredicate);
            }

            if (minPrice != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (minStock != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("stock"), minStock));
            }

            if (maxStock != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("stock"), maxStock));
            }

            if (categoryId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("category").get("id"), categoryId));
            }

            return predicate;
        };
    }
}
