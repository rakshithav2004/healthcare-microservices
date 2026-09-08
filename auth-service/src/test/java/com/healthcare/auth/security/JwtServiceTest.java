package com.healthcare.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

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

        String userId = jwtService.extractUserId(token);

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

        String role = jwtService.extractRole(token);

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