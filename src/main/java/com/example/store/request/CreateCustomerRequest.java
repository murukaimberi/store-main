package com.example.store.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(@NotBlank String name) {}
