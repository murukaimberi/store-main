package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecs {
    public static Specification<Customer> nameContaining(final String query) {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
        };
    }

    public static Specification<Customer> all() {
        return (root, cq, cb) -> {
            cq.distinct(true);
            return null;
        };
    }
}
