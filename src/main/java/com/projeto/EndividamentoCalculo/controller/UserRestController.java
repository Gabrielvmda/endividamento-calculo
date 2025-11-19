package com.projeto.EndividamentoCalculo.controller;

import com.projeto.EndividamentoCalculo.model.User;
import com.projeto.EndividamentoCalculo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    // Listar todos
    @GetMapping
    public List<User> listAll() {
        return userRepository.findAll();
    }

    // Pegar por id
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        Optional<User> u = userRepository.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Criar novo usuário (ID auto gerado)
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        user.setId(null);
        User saved = userRepository.save(user);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(saved);
    }

    // Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User incoming) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User u = opt.get();
        u.setName(incoming.getName());
        u.setEmail(incoming.getEmail());
        u.setPasswordHash(incoming.getPasswordHash());
        userRepository.save(u);
        return ResponseEntity.ok(u);
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
