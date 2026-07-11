package com.expensetracker.dto;

/**
 * Backing object for the registration form (bound via Thymeleaf's th:object).
 * Deliberately simple / unvalidated with annotations here — RegistrationController
 * does explicit checks so it can show a friendly inline error on the same page.
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String displayName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
