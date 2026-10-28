package com.example.store.controller;

import com.example.store.dto.PaginatedResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.request.CreateProductRequest;
import com.example.store.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<ProductDTO>> getAllProducts(
            @PageableDefault(size = 20) final Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable final Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody @Valid final CreateProductRequest request) {
        return productService.save(request);
    }
}
