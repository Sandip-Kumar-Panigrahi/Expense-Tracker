package com.expensetracker.model;

import jakarta.persistence.*;

/**
 * JPA entity mapped to the "categories" table.
 * Tracks every category name in use (the default set plus any custom
 * ones a user has typed in), so the front end can offer them as options.
 */
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name;

    public Category() {
        // required no-arg constructor for JPA
    }

    public Category(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
