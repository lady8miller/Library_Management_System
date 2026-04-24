package com.example.demo10.dto;

// ===== Auth DTOs =====

public class AuthDTOs {

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class RegisterRequest {
        public String username;
        public String password;
        public String email;
        public String role; // USER, MANAGER, ADMIN
    }

    public static class AuthResponse {
        public String token;
        public String username;
        public String role;

        public AuthResponse(String token, String username, String role) {
            this.token = token;
            this.username = username;
            this.role = role;
        }
    }
}