package com.example.demo10.service;

import com.example.demo10.dto.AuthDTOs.*;
import com.example.demo10.entity.User;
import com.example.demo10.repository.UserRepository;
import com.example.demo10.security.jwt.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username)) {
            throw new RuntimeException("Username already taken: " + request.username);
        }
        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("Email already in use: " + request.email);
        }

        // Default role is USER if not specified
        String role = (request.role != null && !request.role.isBlank())
                ? request.role.toUpperCase()
                : "USER";

        User user = new User(
                request.username,
                passwordEncoder.encode(request.password),
                request.email,
                role
        );
        userRepository.save(user);
        return "User registered successfully with role: " + role;
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username, request.password)
        );

        User user = userRepository.findByUsername(request.username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(request.username);
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}