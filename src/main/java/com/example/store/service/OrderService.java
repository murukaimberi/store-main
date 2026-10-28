package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateOrderRequest;

import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(final CreateOrderRequest request);

    PaginatedResponse<OrderDTO> getAllOrders(final Pageable pageable);

    OrderDTO getOrderById(Long id);
}
