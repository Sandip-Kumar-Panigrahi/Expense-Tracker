package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpensesResponse;
import com.expensetracker.dto.ImportRequest;
import com.expensetracker.dto.SingleExpenseResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST API for the expense tracker.
 *
 *   GET    /api/expenses                  -> { expenses, categories }
 *   GET    /api/expenses?period=week       -> filtered to the week containing today
 *   GET    /api/expenses?period=month&year=2026&month=6
 *   GET    /api/expenses?period=quarter&year=2026&quarter=2
 *   GET    /api/expenses?period=year&year=2026
 *   GET    /api/expenses?from=2026-01-01&to=2026-03-31
 *   POST   /api/expenses                   -> add one
 *   PUT    /api/expenses/{id}              -> update one
 *   DELETE /api/expenses/{id}              -> delete one
 *   POST   /api/import                     -> replace all data
 *   GET    /api/health                     -> liveness check
 */
@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    public ExpenseController(ExpenseService expenseService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
    }

    @GetMapping("/expenses")
    public ExpensesResponse getExpenses(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer quarter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Expense> expenses;

        if (from != null || to != null) {
            LocalDate start = from != null ? from : LocalDate.of(2000, 1, 1);
            LocalDate end = to != null ? to : LocalDate.now();
            expenses = expenseService.getByRange(start, end);
        } else if ("week".equalsIgnoreCase(period)) {
            LocalDate[] range = ExpenseService.weekRange(LocalDate.now());
            expenses = expenseService.getByRange(range[0], range[1]);
        } else if ("month".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now();
            LocalDate[] range = ExpenseService.monthRange(
                    year != null ? year : now.getYear(),
                    month != null ? month : now.getMonthValue()
            );
            expenses = expenseService.getByRange(range[0], range[1]);
        } else if ("quarter".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now();
            int q = quarter != null ? quarter : ((now.getMonthValue() - 1) / 3 + 1);
            LocalDate[] range = ExpenseService.quarterRange(year != null ? year : now.getYear(), q);
            expenses = expenseService.getByRange(range[0], range[1]);
        } else if ("year".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now();
            LocalDate[] range = ExpenseService.yearRange(year != null ? year : now.getYear());
            expenses = expenseService.getByRange(range[0], range[1]);
        } else {
            // "all" or no filter given
            expenses = expenseService.getAll();
        }

        return new ExpensesResponse(expenses, categoryService.getAllNames());
    }

    @PostMapping("/expenses")
    public ResponseEntity<SingleExpenseResponse> addExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense saved = expenseService.add(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SingleExpenseResponse(saved, categoryService.getAllNames()));
    }

    @PutMapping("/expenses/{id}")
    public SingleExpenseResponse updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        Expense saved = expenseService.update(id, request);
        return new SingleExpenseResponse(saved, categoryService.getAllNames());
    }

    @DeleteMapping("/expenses/{id}")
    public Map<String, Boolean> deleteExpense(@PathVariable Long id) {
        expenseService.delete(id);
        return Map.of("ok", true);
    }

    @PostMapping("/import")
    public ExpensesResponse importData(@RequestBody ImportRequest request) {
        List<Expense> expenses = expenseService.importAll(request);
        return new ExpensesResponse(expenses, categoryService.getAllNames());
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "time", java.time.Instant.now().toString());
    }
}
