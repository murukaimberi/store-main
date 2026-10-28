package com.example.store.repository;

import com.example.store.TestcontainersConfig;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager em;

    private Customer persistCustomer(final String name) {
        Customer customer = new Customer();
        customer.setName(name);
        return em.persist(customer);
    }

    @Test
    void findAllWithCustomer_loadsCustomer() {
        Customer customer = persistCustomer("Order Query Customer");
        Order order = new Order();
        order.setDescription("Query test order");
        order.setCustomer(customer);
        em.persistAndFlush(order);
        em.clear();

        List<Order> results =
                orderRepository.findAllWithCustomer(Pageable.unpaged()).getContent();

        assertThat(results).isNotEmpty();
        Order found = results.stream()
                .filter(o -> o.getDescription().equals("Query test order"))
                .findFirst()
                .orElseThrow();
        assertThat(found.getCustomer()).isNotNull();
        assertThat(found.getProducts()).isNotNull();
    }

    @Test
    void findByIdWithDetails_existingOrder_returnsWithCustomer() {
        Customer customer = persistCustomer("Detail Customer");
        Order order = new Order();
        order.setDescription("Detail order");
        order.setCustomer(customer);
        Order saved = em.persistAndFlush(order);
        em.clear();

        Optional<Order> result = orderRepository.findByIdWithDetails(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCustomer()).isNotNull();
        assertThat(result.get().getProducts()).isNotNull();
    }

    @Test
    void findByIdWithDetails_nonExistent_returnsEmpty() {
        Optional<Order> result = orderRepository.findByIdWithDetails(999999999L);

        assertThat(result).isEmpty();
    }
}
