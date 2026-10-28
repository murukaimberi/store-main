package com.example.store.service.impl;

import com.example.store.config.CacheConfig;
import com.example.store.dto.PaginatedResponse;
import com.example.store.dto.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import com.example.store.request.CreateProductRequest;
import com.example.store.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = CacheConfig.PRODUCTS_CACHE, key = "'id:' + #id")
    @Override
    public ProductDTO findById(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository
                .findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Cacheable(
            value = CacheConfig.PRODUCTS_CACHE,
            key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
    @Override
    public PaginatedResponse<ProductDTO> findAll(final Pageable pageable) {
        log.debug("Request to find all products");
        return PaginatedResponse.from(productRepository.findAll(pageable).map(productMapper::toDto));
    }

    @Transactional
    @CacheEvict(value = CacheConfig.PRODUCTS_CACHE, allEntries = true)
    @Override
    public ProductDTO save(final CreateProductRequest request) {
        log.debug("Request to save Product : {}", request);
        Product product = new Product();
        product.setDescription(request.description());
        return productMapper.toDto(productRepository.save(product));
    }
}
