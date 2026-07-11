package com.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Shape of the JSON body expected on POST /api/expenses and PUT /api/expenses/{id}.
 * Bean Validation annotations here are enforced automatically by Spring
 * before the controller method body even runs.
 */
public class ExpenseRequest {

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be a positive number")
    private BigDecimal amount;

    @NotBlank(message = "category is required")
    @Size(max = 60, message = "category must be 60 characters or fewer")
    private String category;

    @Size(max = 300, message = "note must be 300 characters or fewer")
    private String note;

    @NotNull(message = "date is required (format yyyy-MM-dd)")
    private LocalDate date;

    public ExpenseRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
