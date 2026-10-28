package com.example.store.service.impl;

import com.example.store.config.CacheConfig;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.request.CreateOrderRequest;
import com.example.store.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = CacheConfig.ORDERS_CACHE, allEntries = true),
                @CacheEvict(value = CacheConfig.CUSTOMERS_CACHE, allEntries = true),
                @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
            })
    @Override
    public OrderDTO createOrder(final CreateOrderRequest request) {
        log.debug("Request to create order : {}", request);
        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.customerId()));
        Set<Product> products = request.productIds() != null
                ? new HashSet<>(productRepository.findAllById(request.productIds()))
                : new HashSet<>();
        Order order = new Order();
        order.setDescription(request.description());
        order.setProducts(products);
        order.setCustomer(customer);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Cacheable(
            value = CacheConfig.ORDERS_CACHE,
            key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    @Override
    public PaginatedResponse<OrderDTO> getAllOrders(final Pageable pageable) {
        return PaginatedResponse.from(
                orderRepository.findAllWithCustomer(pageable).map(orderMapper::toDto));
    }

    @Cacheable(value = CacheConfig.ORDERS_CACHE, key = "'id:' + #id")
    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository
                .findByIdWithDetails(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}
