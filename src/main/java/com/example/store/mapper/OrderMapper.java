package com.example.store.mapper;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {}
