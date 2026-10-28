package com.example.store.service;

import com.example.store.dto.PaginatedResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.request.CreateProductRequest;

import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO findById(Long id);

    PaginatedResponse<ProductDTO> findAll(Pageable pageable);

    ProductDTO save(final CreateProductRequest request);
}
