package com.example.store.controller;

import com.example.store.TestcontainersConfig;
import com.example.store.dto.CustomerDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateCustomerRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
class CustomerControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllCustomers_returnsOkWithPage() {
        ResponseEntity<PaginatedResponse<CustomerDTO>> response = restTemplate.exchange(
                "/customer", org.springframework.http.HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isNotNull();
    }

    @Test
    void searchCustomersByName_returnsMatchingCustomers() {
        restTemplate.postForEntity("/customer", new CreateCustomerRequest("Searchable Person"), CustomerDTO.class);

        ResponseEntity<PaginatedResponse<CustomerDTO>> response = restTemplate.exchange(
                "/customer?name=Searchable",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isNotEmpty();
        assertThat(response.getBody().content()).allMatch(c -> c.name().contains("Searchable"));
    }

    @Test
    void searchCustomersByName_blankParam_returnsAllCustomers() {
        ResponseEntity<PaginatedResponse<CustomerDTO>> response = restTemplate.exchange(
                "/customer?name=",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isNotNull();
    }

    @Test
    void searchCustomersByName_noMatch_returnsEmptyPage() {
        ResponseEntity<PaginatedResponse<CustomerDTO>> response = restTemplate.exchange(
                "/customer?name=zzz_no_match_xyz",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isEmpty();
        assertThat(response.getBody().totalElements()).isZero();
    }

    @Test
    void createCustomer_validRequest_returnsCreated() {
        ResponseEntity<CustomerDTO> response =
                restTemplate.postForEntity("/customer", new CreateCustomerRequest("New Customer"), CustomerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("New Customer");
    }

    @Test
    void createCustomer_blankName_returnsUnprocessableEntity() {
        ResponseEntity<Object> response =
                restTemplate.postForEntity("/customer", new CreateCustomerRequest(""), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
