package com.careermatch.exception;

/**
 * Exception thrown when user input validation fails.
 * Demonstrates custom exception hierarchy and meaningful error messaging.
 */
public class ValidationException extends Exception {
    private final String fieldName;

    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
    }

    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
