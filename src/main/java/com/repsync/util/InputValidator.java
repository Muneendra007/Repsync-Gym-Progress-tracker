package com.repsync.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input.
 * Demonstrates: Exception Handling (throws ValidationException on bad input)
 */
public class InputValidator {

    // Email pattern: basic email validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Validate that a string is not null or empty.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validate email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength.
     * Must be at least 6 characters long.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validate username.
     * Must be 3-50 characters, only letters, numbers, and underscores.
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("[A-Za-z0-9_]{3,50}");
    }

    /**
     * Validate that a number is positive.
     */
    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    /**
     * Validate that a number is within a range.
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Try to parse a string as an integer.
     * Returns the integer value, or -1 if parsing fails.
     */
    public static int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Try to parse a string as a double.
     * Returns the double value, or -1 if parsing fails.
     */
    public static double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
