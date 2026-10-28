package com.example.store.mapper;

import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(
            target = "orderIds",
            expression =
                    "java(product.getOrders().stream().map(com.example.store.entity.Order::getId).collect(java.util.stream.Collectors.toList()))")
    ProductDTO toDto(Product product);
}
