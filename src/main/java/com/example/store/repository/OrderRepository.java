package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.customer")
    Page<Order> findAllWithCustomer(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.products WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}
