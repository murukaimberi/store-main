package com.example.store.service.impl;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.service.CustomerService;
import com.example.store.repository.CustomerSpecs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        log.debug("Request to get all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDTO)
                .toList();
    }

    @Override
    public List<CustomerDTO> getCustomerByName(String name) {
        log.debug("Request to get customer by name: {}", name);
        if (StringUtils.hasText(name)) {
            Specification<Customer> spec = CustomerSpecs.hasNameLike(name);
            List<Customer> allCustomer = customerRepository.findAll();
            List<Customer> customerByName = customerRepository.findAll(spec);
            return customerRepository.findAll(spec).stream()
                    .map(customerMapper::customerToCustomerDTO)
                    .toList();
        }
        return List.of();
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        log.debug("Request to create customer: {}", customerDTO);
        Customer customer = customerMapper.customerDTOToCustomer(customerDTO);
        customer = customerRepository.save(customer);
        return customerMapper.customerToCustomerDTO(customer);
    }
}
