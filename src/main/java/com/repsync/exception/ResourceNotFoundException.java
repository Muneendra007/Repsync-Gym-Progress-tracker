package com.repsync.exception;

/**
 * Custom runtime exception thrown when a requested database resource (User, Exercise, etc.) is not found.
 * Mapped to HTTP 404 NOT FOUND by GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
