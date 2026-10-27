package com.example.store.service;

import com.example.store.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();
    List<CustomerDTO> getCustomerByName(String name);
    CustomerDTO createCustomer(CustomerDTO customerDTO);
}
