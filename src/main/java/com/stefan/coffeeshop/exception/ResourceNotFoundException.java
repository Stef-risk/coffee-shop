package com.stefan.coffeeshop.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " 不存在，ID: " + id);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " 不存在，" + field + ": " + value);
    }
}
