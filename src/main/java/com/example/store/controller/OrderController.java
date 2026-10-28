package com.example.store.controller;

import com.example.store.dto.OrderDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateOrderRequest;
import com.example.store.service.OrderService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<OrderDTO>> getAllOrders(final Pageable pageable) {
        return ResponseEntity.ok().body(orderService.getAllOrders(pageable));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody @Valid final CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok().body(orderService.getOrderById(id));
    }
}
