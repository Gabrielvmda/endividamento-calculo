package com.projeto.EndividamentoCalculo.repository;

import com.projeto.EndividamentoCalculo.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
}

