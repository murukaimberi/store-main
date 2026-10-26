package com.example.store.service;

import com.example.store.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    List<OrderDTO> getAllOrders();

    Optional<OrderDTO> getOrderById(Long id);
}
