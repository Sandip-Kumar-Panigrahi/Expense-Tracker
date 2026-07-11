package com.expensetracker.exception;

/**
 * Thrown when an expense id referenced in a PUT or DELETE request
 * doesn't exist. Caught by GlobalExceptionHandler and turned into a
 * 404 response with a JSON {"error": "..."} body.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
