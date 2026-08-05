package com.repsync.util.exceptions;

/**
 * Custom exception for authentication errors.
 * Thrown when login fails, registration has issues, etc.
 */
public class AuthException extends Exception {

    public AuthException(String message) {
        super(message);
    }
}
