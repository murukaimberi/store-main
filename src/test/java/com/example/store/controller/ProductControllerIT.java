package com.example.store.controller;

import com.example.store.TestcontainersConfig;
import com.example.store.dto.PaginatedResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.request.CreateProductRequest;

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
class ProductControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllProducts_returnsOkWithPage() {
        ResponseEntity<PaginatedResponse<ProductDTO>> response = restTemplate.exchange(
                "/products", org.springframework.http.HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().content()).isNotNull();
    }

    @Test
    void getProductById_existingProduct_returnsProduct() {
        ResponseEntity<ProductDTO> created =
                restTemplate.postForEntity("/products", new CreateProductRequest("A widget"), ProductDTO.class);
        assertThat(created.getBody()).isNotNull();
        Long productId = created.getBody().id();

        ResponseEntity<ProductDTO> response = restTemplate.getForEntity("/products/" + productId, ProductDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(productId);
        assertThat(response.getBody().description()).isEqualTo("A widget");
        assertThat(response.getBody().orderIds()).isNotNull();
    }

    @Test
    void getProductById_nonExistent_returnsNotFound() {
        ResponseEntity<Object> response = restTemplate.getForEntity("/products/999999999", Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createProduct_validRequest_returnsCreated() {
        ResponseEntity<ProductDTO> response =
                restTemplate.postForEntity("/products", new CreateProductRequest("New product"), ProductDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("New product");
        assertThat(response.getBody().orderIds()).isNotNull().isEmpty();
    }

    @Test
    void createProduct_blankDescription_returnsUnprocessableEntity() {
        ResponseEntity<Object> response =
                restTemplate.postForEntity("/products", new CreateProductRequest(""), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
