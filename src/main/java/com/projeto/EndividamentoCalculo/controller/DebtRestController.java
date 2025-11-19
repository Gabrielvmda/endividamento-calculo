package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.model.Debt;
import com.projeto.EndividamentoCalculo.repository.DebtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/debts")
public class DebtRestController {

    @Autowired
    private DebtRepository debtRepository;

    // GET ALL
    @GetMapping("")
    public ResponseEntity<List<Debt>> listAll() {
        return ResponseEntity.ok(debtRepository.findAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Debt> getById(@PathVariable Long id) {
        Optional<Debt> opt = debtRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET BY USER
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Debt>> getByUser(@PathVariable Long userId) {
        List<Debt> list = debtRepository.findByUserId(userId);
        return ResponseEntity.ok(list);
    }

    // CREATE
    @PostMapping("")
    public ResponseEntity<Debt> create(@RequestBody Debt d) {
        Debt saved = debtRepository.save(d);
        return ResponseEntity.created(URI.create("/api/debts/" + saved.getId())).body(saved);
    }

    // UPDATE (Debt)
    @PutMapping("/{id}")
    public ResponseEntity<Debt> update(@PathVariable Long id, @RequestBody Debt in) {
        if (!debtRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // garante que o objeto que vem do body tenha o id correto
        in.setId(id);
        Debt saved = debtRepository.save(in);
        return ResponseEntity.ok(saved);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!debtRepository.existsById(id)) return ResponseEntity.notFound().build();
        debtRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
