package com.expensetracker.service;

import com.expensetracker.model.Category;
import com.expensetracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    public static final List<String> DEFAULT_CATEGORIES = List.of(
            "Shopping", "Traveling", "Eating", "Bills",
            "Health", "Entertainment", "Education", "Other"
    );

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** Ensures every default category exists in the table. Safe to call repeatedly. */
    public void seedDefaultsIfEmpty() {
        if (categoryRepository.count() == 0) {
            DEFAULT_CATEGORIES.forEach(name -> categoryRepository.save(new Category(name)));
        }
    }

    /** Adds the category if it doesn't already exist (case-insensitive check). */
    public void ensureExists(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        categoryRepository.findByNameIgnoreCase(name.trim())
                .orElseGet(() -> categoryRepository.save(new Category(name.trim())));
    }

    public List<String> getAllNames() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    /** Wipes all categories and replaces with defaults + whatever is passed in. */
    public void replaceAll(List<String> incoming) {
        categoryRepository.deleteAll();
        java.util.LinkedHashSet<String> merged = new java.util.LinkedHashSet<>(DEFAULT_CATEGORIES);
        if (incoming != null) {
            incoming.stream()
                    .filter(n -> n != null && !n.isBlank())
                    .map(String::trim)
                    .forEach(merged::add);
        }
        merged.forEach(name -> categoryRepository.save(new Category(name)));
    }
}
