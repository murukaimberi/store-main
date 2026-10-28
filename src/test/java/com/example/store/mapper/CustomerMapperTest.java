package com.example.store.mapper;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = org.mapstruct.factory.Mappers.getMapper(CustomerMapper.class);

    @Test
    void toCustomerDTO_mapsAllFields() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Jane Doe");

        Order order = new Order();
        order.setId(10L);
        order.setDescription("A book");
        customer.getOrders().add(order);

        CustomerDTO dto = mapper.toDto(customer);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Jane Doe");
        assertThat(dto.orders()).hasSize(1);
        assertThat(dto.orders().get(0).id()).isEqualTo(10L);
        assertThat(dto.orders().get(0).description()).isEqualTo("A book");
    }

    @Test
    void toCustomerDTOs_mapsAllElements() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("First");

        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Second");

        List<CustomerDTO> dtos = mapper.toDtoList(List.of(c1, c2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(CustomerDTO::name).containsExactly("First", "Second");
    }
}
