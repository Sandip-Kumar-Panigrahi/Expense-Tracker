package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Shape of the JSON body expected on POST /api/import.
 * Used by the front end's "Import JSON" button to replace all
 * server-side data in one shot (e.g. restoring from a prior export).
 */
public class ImportRequest {

    private List<ImportItem> expenses;
    private List<String> categories;

    public List<ImportItem> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ImportItem> expenses) {
        this.expenses = expenses;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /** One row inside the imported expenses array. Deliberately unvalidated
     *  at the field level — invalid rows are skipped rather than failing
     *  the whole import, matching the behavior of the earlier Node version. */
    public static class ImportItem {
        private BigDecimal amount;
        private String category;
        private String note;
        private LocalDate date;

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
}
