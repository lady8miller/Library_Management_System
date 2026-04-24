package com.example.demo10.config;

import com.example.demo10.security.jwt.JwtFilter;
import com.example.demo10.security.jwt.JwtUtil;
import com.example.demo10.security.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints — no token required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/books/search").permitAll()
                        .requestMatchers("/api/books/{id}/reviews").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // User endpoints — any authenticated user
                        .requestMatchers("/api/books").permitAll()
                        .requestMatchers("/api/borrows/my/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/borrows/request/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/borrows/return/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/books/{id}/review").hasAnyRole("USER", "MANAGER", "ADMIN")

                        // Manager endpoints — approve/reject borrow requests
                        .requestMatchers("/api/borrows/pending").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/borrows/approve/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/borrows/reject/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/borrows/all").hasAnyRole("MANAGER", "ADMIN")

                        // Admin endpoints — full access
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/books/create").hasRole("ADMIN")
                        .requestMatchers("/api/books/update/**").hasRole("ADMIN")
                        .requestMatchers("/api/books/delete/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // for H2 console
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}