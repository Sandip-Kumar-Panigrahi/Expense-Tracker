package com.expensetracker.repository;

import com.expensetracker.model.AppUser;
import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Expense entities. Every query here is
 * scoped to a specific owner (AppUser), so one person's data is never
 * visible to another — enforced at the database query level, not just
 * in the UI.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOwnerOrderByDateDescIdDesc(AppUser owner);

    List<Expense> findByOwnerAndDateBetweenOrderByDateAsc(AppUser owner, LocalDate start, LocalDate end);

    Optional<Expense> findByIdAndOwner(Long id, AppUser owner);

    boolean existsByIdAndOwner(Long id, AppUser owner);

    long deleteByOwner(AppUser owner);
}
