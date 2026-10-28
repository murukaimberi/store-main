package com.example.store.entity;

import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private String description;

    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private Set<Order> orders = new HashSet<>();
}
