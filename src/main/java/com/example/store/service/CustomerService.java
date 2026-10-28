package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateCustomerRequest;

import org.springframework.data.domain.Pageable;

public interface CustomerService {
    PaginatedResponse<CustomerDTO> getAllCustomers(final Pageable pageable);

    PaginatedResponse<CustomerDTO> getCustomerByName(final String name, final Pageable pageable);

    CustomerDTO createCustomer(final CreateCustomerRequest request);
}
