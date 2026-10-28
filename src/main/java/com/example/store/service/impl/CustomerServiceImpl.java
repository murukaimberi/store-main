package com.example.store.service.impl;

import com.example.store.config.CacheConfig;
import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.CustomerSpecs;
import com.example.store.request.CreateCustomerRequest;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Cacheable(
            value = CacheConfig.CUSTOMERS_CACHE,
            key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    @Override
    public PaginatedResponse<CustomerDTO> getAllCustomers(final Pageable pageable) {
        log.debug("Request to get all customers");
        return PaginatedResponse.from(customerRepository.findAll(pageable).map(customerMapper::toDto));
    }

    @Cacheable(
            value = CacheConfig.CUSTOMERS_CACHE,
            key =
                    "'name:' + #name.toLowerCase() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    @Override
    public PaginatedResponse<CustomerDTO> getCustomerByName(final String name, final Pageable pageable) {
        log.debug("Request to get customer by name: {}", name);
        return PaginatedResponse.from(customerRepository
                .findAll(CustomerSpecs.nameContaining(name), pageable)
                .map(customerMapper::toDto));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.CUSTOMERS_CACHE, allEntries = true)
    @Override
    public CustomerDTO createCustomer(final CreateCustomerRequest request) {
        log.debug("Request to create customer: {}", request);
        Customer customer = new Customer();
        customer.setName(request.name());
        customer = customerRepository.save(customer);
        return customerMapper.toDto(customer);
    }
}
