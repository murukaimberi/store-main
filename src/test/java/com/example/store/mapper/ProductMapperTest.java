package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = org.mapstruct.factory.Mappers.getMapper(ProductMapper.class);

    @Test
    void toProductDTO_mapsFieldsAndOrderIds() {
        Order order1 = new Order();
        order1.setId(10L);
        order1.setDescription("Order 10");

        Order order2 = new Order();
        order2.setId(20L);
        order2.setDescription("Order 20");
        Set<Order> orders = new HashSet<>();
        orders.add(order1);
        orders.add(order2);
        Product product = new Product();
        product.setId(5L);
        product.setDescription("Sprocket");
        product.setOrders(orders);

        ProductDTO dto = mapper.toDto(product);

        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.description()).isEqualTo("Sprocket");
        assertThat(dto.orderIds()).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    void toProductDTO_noOrders_returnsEmptyOrderIds() {
        Product product = new Product();
        product.setId(1L);
        product.setDescription("Standalone");

        ProductDTO dto = mapper.toDto(product);

        assertThat(dto.orderIds()).isEmpty();
    }

    @Test
    void toProductDTOs_mapsAllElements() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setDescription("Alpha");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setDescription("Beta");

        List<ProductDTO> dtos = mapper.toDtoList(List.of(p1, p2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos).extracting(ProductDTO::description).containsExactly("Alpha", "Beta");
    }
}
