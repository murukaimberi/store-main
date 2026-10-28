package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateCustomerRequest;
import com.example.store.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<CustomerDTO>> getAllCustomers(
            @RequestParam(required = false) final String name, @PageableDefault(size = 20) final Pageable pageable) {
        if (StringUtils.hasText(name)) {
            return ResponseEntity.ok(customerService.getCustomerByName(name, pageable));
        }
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody @Valid final CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }
}
