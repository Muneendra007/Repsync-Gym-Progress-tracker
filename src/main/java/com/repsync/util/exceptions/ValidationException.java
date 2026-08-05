package com.repsync.util.exceptions;

/**
 * Custom exception for input validation errors.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
