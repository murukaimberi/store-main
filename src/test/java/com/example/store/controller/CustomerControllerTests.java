package com.example.store.controller;

import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.liquibase.contexts=test")
@Import(PostgresqlConfiguration.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor
@Slf4j
@Transactional
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        customer = new Customer();
        customer.setName("John Doe");
        customer = customerRepository.save(customer);

        Customer customerOne = new Customer();
        customerOne.setName("Jane Doe");
        customerOne = customerRepository.save(customerOne);
    }

    @Test
    void testCreateCustomer() throws Exception {
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name").value("John Doe"))
                .andExpect(jsonPath("$..name").value("Jane Doe"))
        ;
        ;
    }

    @Test
    void whenGetCustomerByName_ThenReturnCustomer() throws Exception {
        mockMvc.perform(get("/customer/search?name=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name").value("John Doe"))
                .andDo(print());
    }

    @Test
    void whenGetCustomerByNameNotExists_ThenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/customer/search?name='Peter'"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
