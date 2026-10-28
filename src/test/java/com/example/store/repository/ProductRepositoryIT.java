package com.example.store.repository;

import com.example.store.TestcontainersConfig;
import com.example.store.entity.Product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAll_paginated_returnsProducts() {
        Product product = new Product();
        product.setDescription("Findable product");
        em.persistAndFlush(product);
        em.clear();

        List<Product> results = productRepository.findAll(PageRequest.of(0, 20)).getContent();

        assertThat(results).anyMatch(p -> p.getDescription().equals("Findable product"));
        results.forEach(p -> assertThat(p.getOrders()).isNotNull());
    }

    @Test
    void findByIdWithOrders_existingProduct_returnsWithOrders() {
        Product product = new Product();
        product.setDescription("Single product");
        Product saved = em.persistAndFlush(product);
        em.clear();

        Optional<Product> result = productRepository.findByIdWithOrders(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Single product");
        assertThat(result.get().getOrders()).isNotNull();
    }

    @Test
    void findByIdWithOrders_nonExistent_returnsEmpty() {
        Optional<Product> result = productRepository.findByIdWithOrders(999999999L);

        assertThat(result).isEmpty();
    }
}
