package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ImportRequest;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.AppUser;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Core business logic: create/read/update/delete expenses, plus
 * convenience lookups for weekly / monthly / quarterly / yearly ranges.
 *
 * Every method here operates only on the currently logged-in user's own
 * data — getCurrentUser() reads who's authenticated from Spring Security's
 * SecurityContext (populated automatically after a successful login), and
 * every repository call is scoped to that user. There is no code path
 * here that can return or modify another user's expenses.
 */
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryService categoryService, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    /** Resolves the AppUser record for whoever is currently logged in. */
    private AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("Not authenticated");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public List<Expense> getAll() {
        return expenseRepository.findAllByOwnerOrderByDateDescIdDesc(getCurrentUser());
    }

    public Expense getByIdOrThrow(Long id) {
        AppUser current = getCurrentUser();
        return expenseRepository.findByIdAndOwner(id, current)
                .orElseThrow(() -> new ResourceNotFoundException("expense not found"));
    }

    @Transactional
    public Expense add(ExpenseRequest request) {
        AppUser current = getCurrentUser();
        categoryService.ensureExists(request.getCategory());
        Expense expense = new Expense(
                request.getAmount(),
                request.getCategory().trim(),
                request.getNote() == null ? "" : request.getNote().trim(),
                request.getDate(),
                current
        );
        return expenseRepository.save(expense);
    }

    @Transactional
    public Expense update(Long id, ExpenseRequest request) {
        Expense existing = getByIdOrThrow(id); // already scoped to current user; 404s otherwise
        categoryService.ensureExists(request.getCategory());
        existing.setAmount(request.getAmount());
        existing.setCategory(request.getCategory().trim());
        existing.setNote(request.getNote() == null ? "" : request.getNote().trim());
        existing.setDate(request.getDate());
        return expenseRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        AppUser current = getCurrentUser();
        if (!expenseRepository.existsByIdAndOwner(id, current)) {
            throw new ResourceNotFoundException("expense not found");
        }
        expenseRepository.deleteById(id);
    }

    /** Replaces all of the CURRENT user's data only — never touches other accounts. */
    @Transactional
    public List<Expense> importAll(ImportRequest request) {
        AppUser current = getCurrentUser();
        expenseRepository.deleteByOwner(current);

        List<String> categoryNames = request.getExpenses() == null
                ? List.of()
                : request.getExpenses().stream()
                    .map(ImportRequest.ImportItem::getCategory)
                    .filter(c -> c != null && !c.isBlank())
                    .toList();

        java.util.LinkedHashSet<String> allCategories = new java.util.LinkedHashSet<>();
        if (request.getCategories() != null) allCategories.addAll(request.getCategories());
        allCategories.addAll(categoryNames);
        allCategories.forEach(categoryService::ensureExists);

        if (request.getExpenses() != null) {
            for (ImportRequest.ImportItem item : request.getExpenses()) {
                if (item.getAmount() == null || item.getAmount().signum() <= 0) continue;
                if (item.getCategory() == null || item.getCategory().isBlank()) continue;
                if (item.getDate() == null) continue;
                Expense expense = new Expense(
                        item.getAmount(),
                        item.getCategory().trim(),
                        item.getNote() == null ? "" : item.getNote().trim(),
                        item.getDate(),
                        current
                );
                expenseRepository.save(expense);
            }
        }

        return getAll();
    }

    // ----------------- Period range helpers -----------------

    public static LocalDate[] weekRange(LocalDate anyDayInWeek) {
        LocalDate monday = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        return new LocalDate[]{monday, sunday};
    }

    public static LocalDate[] monthRange(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return new LocalDate[]{start, end};
    }

    public static LocalDate[] quarterRange(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = start.plusMonths(3).minusDays(1);
        return new LocalDate[]{start, end};
    }

    public static LocalDate[] yearRange(int year) {
        return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)};
    }

    public List<Expense> getByRange(LocalDate start, LocalDate end) {
        return expenseRepository.findByOwnerAndDateBetweenOrderByDateAsc(getCurrentUser(), start, end);
    }
}
