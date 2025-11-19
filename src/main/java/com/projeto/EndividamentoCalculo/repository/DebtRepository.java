package com.projeto.EndividamentoCalculo.repository;

import com.projeto.EndividamentoCalculo.model.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserId(Long userId);
}
