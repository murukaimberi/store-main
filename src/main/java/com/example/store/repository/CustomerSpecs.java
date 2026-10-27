package com.example.store.repository;

import com.example.store.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecs {
    public static Specification<Customer> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return null; // no filter
            }
            String pattern = "%" + escapeLike(name.trim().toLowerCase()) + "%";
            return cb.like(cb.lower(root.get("name")), pattern, '\\');
        };
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
