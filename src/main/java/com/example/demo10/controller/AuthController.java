package com.example.demo10.controller;

import com.example.demo10.dto.AuthDTOs.*;
import com.example.demo10.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Body: { "username": "john", "password": "pass123", "email": "john@mail.com", "role": "USER" }
     * Access: Public
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "john", "password": "pass123" }
     * Returns: JWT token + user info
     * Access: Public
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}