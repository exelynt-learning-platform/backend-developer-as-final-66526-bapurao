package com.example.booking.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private String generateTestSecret() {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }

    @Test
    void tokenContainsAndValidatesSubject() {

        String testSecret = generateTestSecret();

        JwtService jwt = new JwtService(
                testSecret,
                3600000
        );

        UserDetails user = User
                .withUsername("alice")
                .password("x")
                .roles("USER")
                .build();

        String token = jwt.generateToken(user);

        assertEquals(
                "alice",
                jwt.extractUsername(token)
        );

        assertTrue(
                jwt.isTokenValid(
                        token,
                        user.getUsername()
                )
        );
    }

    @Test
    void weakSecretRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(
                        "short",
                        1000
                )
        );
    }
}