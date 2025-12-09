package com.farmasystem.backend.controller;

import com.farmasystem.backend.dto.AuthRequest;
import com.farmasystem.backend.dto.AuthResponse;
import com.farmasystem.backend.model.Role; // <--- Faltaba esto
import com.farmasystem.backend.model.User;
import com.farmasystem.backend.repository.UserRepository;
import com.farmasystem.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <--- Para el register
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder; // <--- Para el register
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // 1. Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Buscar usuario en BD (Aquí sí tenemos el fullName)
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        
        // 3. Generar Token
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .fullName(user.getFullName()) // <--- Ahora sí funciona (viene de User, no de AuthRequest)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) { // Usamos User directo para simplificar o crea un RegisterRequest
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.SELLER);
        
        userRepository.save(user);

        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
}