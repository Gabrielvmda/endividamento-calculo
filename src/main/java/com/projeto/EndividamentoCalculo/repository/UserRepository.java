package com.projeto.EndividamentoCalculo.repository;

import com.projeto.EndividamentoCalculo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
