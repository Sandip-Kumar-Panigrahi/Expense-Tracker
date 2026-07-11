package com.expensetracker.exception;

/**
 * Simple JSON error shape: {"error": "message here"}.
 * Kept deliberately minimal to match a predictable contract the
 * front end can rely on for every failure case.
 */
public class ApiError {
    private String error;

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
