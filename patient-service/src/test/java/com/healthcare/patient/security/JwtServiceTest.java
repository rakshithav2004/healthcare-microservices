package com.healthcare.patient.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "HealthcareMicroservicesJwtSecret2026SecureKeyForDevelopmentOnly123456";

    private JwtService jwtService;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);

        secretKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void extractUserId_shouldReturnUserId() {

        String token = createToken(
                "user-123",
                "PATIENT"
        );

        String userId =
                jwtService.extractUserId(token);

        assertThat(userId)
                .isEqualTo("user-123");
    }

    @Test
    void extractRole_shouldReturnRole() {

        String token = createToken(
                "user-123",
                "PATIENT"
        );

        String role =
                jwtService.extractRole(token);

        assertThat(role)
                .isEqualTo("PATIENT");
    }

    @Test
    void extractAllClaims_shouldReturnClaims() {

        String token = createToken(
                "user-123",
                "DOCTOR"
        );

        var claims =
                jwtService.extractAllClaims(token);

        assertThat(claims.getSubject())
                .isEqualTo("user-123");

        assertThat(claims.get("role", String.class))
                .isEqualTo("DOCTOR");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {

        String token = createToken(
                "user-123",
                "PATIENT"
        );

        boolean valid =
                jwtService.isTokenValid(token);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {

        boolean valid =
                jwtService.isTokenValid("invalid-token");

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForTokenSignedWithWrongSecret() {

        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "AnotherDifferentSecretKeyForTesting123456789"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user-123")
                .claim("role", "PATIENT")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(wrongKey)
                .compact();

        boolean valid =
                jwtService.isTokenValid(token);

        assertThat(valid).isFalse();
    }

    private String createToken(
            String userId,
            String role) {

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 3600000)
                )
                .signWith(secretKey)
                .compact();
    }
}