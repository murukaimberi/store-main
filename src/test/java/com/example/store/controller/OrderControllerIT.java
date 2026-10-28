package com.example.store.controller;

import com.example.store.TestcontainersConfig;
import com.example.store.dto.CustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.dto.PaginatedResponse;
import com.example.store.request.CreateCustomerRequest;
import com.example.store.request.CreateOrderRequest;

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
class OrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long createCustomer() {
        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity(
                "/customer", new CreateCustomerRequest("Order Test Customer"), CustomerDTO.class);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().id();
    }

    @Test
    void getAllOrders_returnsOkWithPage() {
        ResponseEntity<PaginatedResponse<OrderDTO>> response = restTemplate.exchange(
                "/order", org.springframework.http.HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isNotNull();
    }

    @Test
    void getOrderById_existingOrder_returnsOrder() {
        Long customerId = createCustomer();
        ResponseEntity<OrderDTO> created = restTemplate.postForEntity(
                "/order", new CreateOrderRequest("Test order", customerId, null), OrderDTO.class);
        assertThat(created.getBody()).isNotNull();
        Long orderId = created.getBody().id();

        ResponseEntity<OrderDTO> response = restTemplate.getForEntity("/order/" + orderId, OrderDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(orderId);
        assertThat(response.getBody().customer()).isNotNull();
        assertThat(response.getBody().products()).isNotNull();
    }

    @Test
    void getOrderById_nonExistent_returnsNotFound() {
        ResponseEntity<Object> response = restTemplate.getForEntity("/order/999999999", Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createOrder_validRequest_returnsCreated() {
        Long customerId = createCustomer();

        ResponseEntity<OrderDTO> response = restTemplate.postForEntity(
                "/order", new CreateOrderRequest("A new order", customerId, null), OrderDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("A new order");
        assertThat(response.getBody().customer().id()).isEqualTo(customerId);
    }

    @Test
    void createOrder_withProducts_returnsCreatedWithProducts() {
        Long customerId = createCustomer();

        com.example.store.dto.ProductDTO product = restTemplate
                .postForEntity(
                        "/products",
                        new com.example.store.request.CreateProductRequest("Widget"),
                        com.example.store.dto.ProductDTO.class)
                .getBody();
        assertThat(product).isNotNull();

        ResponseEntity<OrderDTO> response = restTemplate.postForEntity(
                "/order",
                new CreateOrderRequest("Order with product", customerId, java.util.List.of(product.id())),
                OrderDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().products()).hasSize(1);
    }

    @Test
    void createOrder_nullCustomerId_returnsUnprocessableEntity() {
        ResponseEntity<Object> response =
                restTemplate.postForEntity("/order", new CreateOrderRequest("desc", null, null), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createOrder_blankDescription_returnsUnprocessableEntity() {
        Long customerId = createCustomer();

        ResponseEntity<Object> response =
                restTemplate.postForEntity("/order", new CreateOrderRequest("", customerId, null), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
