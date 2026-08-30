package com.example.booking.config;

import com.example.booking.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // Password encryption
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // REST API does not use CSRF protection
            .csrf(csrf -> csrf.disable())

            // JWT authentication is stateless
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // =========================================
                // PUBLIC ENDPOINTS
                // =========================================

                // Login does not require JWT
                .requestMatchers(
                    "/auth/login"
                ).permitAll()

                // Swagger/OpenAPI does not require JWT
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()


                // =========================================
                // RESOURCE ENDPOINTS
                // =========================================

                // USER + ADMIN can READ resources
                .requestMatchers(
                    HttpMethod.GET,
                    "/resources/**"
                ).hasAnyRole("USER", "ADMIN")

                // Only ADMIN can CREATE resources
                .requestMatchers(
                    HttpMethod.POST,
                    "/resources/**"
                ).hasRole("ADMIN")

                // Only ADMIN can UPDATE resources
                .requestMatchers(
                    HttpMethod.PUT,
                    "/resources/**"
                ).hasRole("ADMIN")

                // Only ADMIN can PARTIALLY UPDATE resources
                .requestMatchers(
                    HttpMethod.PATCH,
                    "/resources/**"
                ).hasRole("ADMIN")

                // Only ADMIN can DELETE resources
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/resources/**"
                ).hasRole("ADMIN")


                // =========================================
                // RESERVATION ENDPOINTS
                // =========================================

                // USER + ADMIN can access reservations
                .requestMatchers(
                    "/reservations/**"
                ).hasAnyRole("USER", "ADMIN")


                // =========================================
                // EVERYTHING ELSE
                // =========================================

                // Any other endpoint requires authentication
                .anyRequest().authenticated()
            )

            // =========================================
            // JWT FILTER
            // =========================================

            // JWT filter executes before Spring's
            // UsernamePasswordAuthenticationFilter
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}