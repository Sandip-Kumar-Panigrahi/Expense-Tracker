package com.expensetracker.dto;

import com.expensetracker.model.Expense;

import java.util.List;

/**
 * Standard envelope returned by GET /api/expenses and other endpoints:
 * the list of expenses plus the current set of known category names.
 */
public class ExpensesResponse {
    private List<Expense> expenses;
    private List<String> categories;

    public ExpensesResponse(List<Expense> expenses, List<String> categories) {
        this.expenses = expenses;
        this.categories = categories;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
