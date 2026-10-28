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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfig.class)
class CustomerRepositoryIT {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAll_withSpec_loadsOrders() {
        Customer customer = new Customer();
        customer.setName("Alice Smith");
        customer = em.persist(customer);

        Order order = new Order();
        order.setDescription("Alice's order");
        order.setCustomer(customer);
        em.persist(order);
        em.flush();
        em.clear();

        List<Customer> results = customerRepository
                .findAll(CustomerSpecs.all(), Pageable.unpaged())
                .getContent();

        assertThat(results).isNotEmpty();
        Customer found = results.stream()
                .filter(c -> c.getName().equals("Alice Smith"))
                .findFirst()
                .orElseThrow();
        assertThat(found.getOrders()).hasSize(1);
    }

    @Test
    void nameContaining_matchesSubstring() {
        Customer customer = new Customer();
        customer.setName("Bob Johnson");
        em.persistAndFlush(customer);
        em.clear();

        List<Customer> results = customerRepository
                .findAll(CustomerSpecs.nameContaining("John"), PageRequest.of(0, 20))
                .getContent();

        assertThat(results).anyMatch(c -> c.getName().equals("Bob Johnson"));
    }

    @Test
    void nameContaining_caseInsensitive() {
        Customer customer = new Customer();
        customer.setName("Carol Williams");
        em.persistAndFlush(customer);
        em.clear();

        List<Customer> results = customerRepository
                .findAll(CustomerSpecs.nameContaining("carol"), PageRequest.of(0, 20))
                .getContent();

        assertThat(results).anyMatch(c -> c.getName().equals("Carol Williams"));
    }

    @Test
    void nameContaining_noMatch_returnsEmpty() {
        List<Customer> results = customerRepository
                .findAll(Specification.where(CustomerSpecs.nameContaining("zzz_no_match_xyz")), PageRequest.of(0, 20))
                .getContent();

        assertThat(results).isEmpty();
    }
}
