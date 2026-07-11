package com.expensetracker.dto;

import com.expensetracker.model.Expense;

import java.util.List;

/**
 * Returned by POST /api/expenses and PUT /api/expenses/{id}: the single
 * expense that was just created or updated, plus the refreshed category list
 * (useful if the request introduced a brand-new custom category).
 */
public class SingleExpenseResponse {
    private Expense expense;
    private List<String> categories;

    public SingleExpenseResponse(Expense expense, List<String> categories) {
        this.expense = expense;
        this.categories = categories;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
