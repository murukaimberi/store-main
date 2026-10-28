package com.example.store.mapper;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = org.mapstruct.factory.Mappers.getMapper(OrderMapper.class);

    @Test
    void toOrderDTO_mapsAllFields() {
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setName("Alice");

        Product product = new Product();
        product.setId(7L);
        product.setDescription("Gadget");

        Order order = new Order();
        order.setId(3L);
        order.setDescription("My order");
        order.setCustomer(customer);
        order.getProducts().add(product);

        OrderDTO dto = mapper.toDto(order);

        assertThat(dto.id()).isEqualTo(3L);
        assertThat(dto.description()).isEqualTo("My order");
        assertThat(dto.customer()).isNotNull();
        assertThat(dto.customer().id()).isEqualTo(2L);
        assertThat(dto.customer().name()).isEqualTo("Alice");
        assertThat(dto.products()).hasSize(1);
        assertThat(dto.products().get(0).id()).isEqualTo(7L);
        assertThat(dto.products().get(0).description()).isEqualTo("Gadget");
    }

    @Test
    void toOrderDTOs_mapsAllElements() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Bob");

        Order o1 = new Order();
        o1.setId(1L);
        o1.setDescription("First");
        o1.setCustomer(customer);

        Order o2 = new Order();
        o2.setId(2L);
        o2.setDescription("Second");
        o2.setCustomer(customer);

        List<OrderDTO> dtos = mapper.toDtoList(List.of(o1, o2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(OrderDTO::description).containsExactly("First", "Second");
    }
}
