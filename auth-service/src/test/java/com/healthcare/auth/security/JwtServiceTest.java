package com.healthcare.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "HealthcareMicroservicesJwtSecret2026SecureKeyForDevelopmentOnly123456";

    private final long expiration =
            3600000L;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(
                secret,
                expiration
        );
    }

    @Test
    void generateToken_shouldCreateValidToken() {

        String token = jwtService.generateToken(
                "user-123",
                "patient@example.com",
                "PATIENT"
        );

        assertThat(token)
                .isNotBlank();
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {

        String token = jwtService.generateToken(
                "user-123",
                "patient@example.com",
                "PATIENT"
        );

        String userId =
                jwtService.extractUserId(token);

        assertThat(userId)
                .isEqualTo("user-123");
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {

        String token = jwtService.generateToken(
                "user-123",
                "patient@example.com",
                "PATIENT"
        );

        String role =
                jwtService.extractRole(token);

        assertThat(role)
                .isEqualTo("PATIENT");
    }

    @Test
    void generateToken_shouldContainUserIdAndRole() {

        String token = jwtService.generateToken(
                "doctor-123",
                "doctor@example.com",
                "DOCTOR"
        );

        assertThat(jwtService.extractUserId(token))
                .isEqualTo("doctor-123");

        assertThat(jwtService.extractRole(token))
                .isEqualTo("DOCTOR");
    }
}