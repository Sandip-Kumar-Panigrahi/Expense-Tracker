package com.expensetracker.model;

import jakarta.persistence.*;

/**
 * A login account. Named "AppUser" (not "User") to avoid clashing with
 * Spring Security's own org.springframework.security.core.userdetails.User class.
 * Each Expense belongs to exactly one AppUser, so every person only ever
 * sees their own data.
 */
@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** Stored as a BCrypt hash — never the plain-text password. */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(length = 100)
    private String displayName;

    public AppUser() {
    }

    public AppUser(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
