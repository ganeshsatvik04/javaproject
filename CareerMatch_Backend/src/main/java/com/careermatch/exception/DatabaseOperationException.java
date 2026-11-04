package com.careermatch.exception;

/**
 * Exception thrown when JDBC database queries or updates fail.
 */
public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
