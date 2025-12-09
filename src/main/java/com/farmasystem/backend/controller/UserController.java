package com.farmasystem.backend.controller;

import com.farmasystem.backend.model.Role;
import com.farmasystem.backend.model.User;
import com.farmasystem.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Permite conexión desde React
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // LISTAR TODOS (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // En producción idealmente devolverías un DTO sin la contraseña
        return ResponseEntity.ok(userRepository.findAll());
    }

    // CREAR NUEVO USUARIO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Si no envía rol, por defecto es SELLER
        if (user.getRole() == null) user.setRole(Role.SELLER);
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    // ELIMINAR USUARIO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (id == null || !userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}