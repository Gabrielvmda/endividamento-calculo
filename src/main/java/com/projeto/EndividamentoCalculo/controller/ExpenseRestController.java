package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.model.Expense;
import com.projeto.EndividamentoCalculo.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    @Autowired
    private ExpenseRepository expenseRepository;

    // GET ALL
    @GetMapping("")
    public ResponseEntity<List<Expense>> listAll() {
        return ResponseEntity.ok(expenseRepository.findAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        Optional<Expense> opt = expenseRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getByUser(@PathVariable Long userId) {
        List<Expense> list = expenseRepository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    // CREATE
    @PostMapping("")
    public ResponseEntity<Expense> create(@RequestBody Expense exp) {
        Expense saved = expenseRepository.save(exp);
        return ResponseEntity.created(URI.create("/api/expenses/" + saved.getId())).body(saved);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Expense> update(@PathVariable Long id, @RequestBody Expense in) {
        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // garante que o objeto que vem do body tenha o id correto
        in.setId(id);
        Expense saved = expenseRepository.save(in);
        return ResponseEntity.ok(saved);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!expenseRepository.existsById(id)) return ResponseEntity.notFound().build();
        expenseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
