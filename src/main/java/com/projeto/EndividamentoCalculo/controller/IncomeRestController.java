package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.model.Income;
import com.projeto.EndividamentoCalculo.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/incomes")
public class IncomeRestController {

    @Autowired
    private IncomeRepository incomeRepository;

    // Todos
    @GetMapping("")
    public ResponseEntity<List<Income>> listAll() {
        List<Income> list = incomeRepository.findAll();
        return ResponseEntity.ok(list);
    }

    // Por id
    @GetMapping("/{id}")
    public ResponseEntity<Income> getById(@PathVariable Long id) {
        Optional<Income> opt = incomeRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Income>> getByUser(@PathVariable Long userId) {
        List<Income> list = incomeRepository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    // Criar
    @PostMapping("")
    public ResponseEntity<Income> create(@RequestBody Income in) {
        Income saved = incomeRepository.save(in);
        return ResponseEntity.created(URI.create("/api/incomes/" + saved.getId())).body(saved);
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<Income> update(@PathVariable Long id, @RequestBody Income in) {
        Optional<Income> opt = incomeRepository.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        Income e = opt.get();
        e.setMonthlyAmount(in.getMonthlyAmount());
        e.setSource(in.getSource());
        e.setUser(in.getUser());
        incomeRepository.save(e);
        return ResponseEntity.ok(e);
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!incomeRepository.existsById(id)) return ResponseEntity.notFound().build();
        incomeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
