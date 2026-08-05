package com.repsync.controller;

import com.repsync.dto.auth.AuthResponse;
import com.repsync.dto.auth.LoginRequest;
import com.repsync.dto.auth.RegisterRequest;
import com.repsync.model.User;
import com.repsync.model.enums.UserRole;
import com.repsync.repository.UserRepository;
import com.repsync.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST Controller for User Authentication (Login & Signup).
 * Exposes endpoints:
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse("Error: Email is already registered!", null));
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse("Error: Username is already taken!", null));
        }

        User user = new User(
                0,
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                UserRole.USER,
                request.getAge(),
                request.getGender(),
                request.getHeightCm(),
                request.getWeightKg(),
                request.getFitnessGoal(),
                request.getExperienceLevel(),
                LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, savedUser));
    }

    @PutMapping("/goal")
    public ResponseEntity<User> updateFitnessGoal(@RequestParam String goal, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            user.setFitnessGoal(com.repsync.model.enums.FitnessGoal.valueOf(goal.toUpperCase()));
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
