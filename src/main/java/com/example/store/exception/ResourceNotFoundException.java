package com.example.store.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(final String resource, final Long id) {
        super(resource + " not found with id: " + id);
    }
}
