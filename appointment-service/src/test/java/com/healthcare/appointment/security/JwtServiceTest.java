package com.healthcare.appointment.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "HealthcareMicroservicesJwtSecret2026SecureKeyForDevelopmentOnly123456";

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret);

        secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void extractUserId_shouldReturnUserId() {

        String token = createToken("patient-123", "PATIENT");

        String userId = jwtService.extractUserId(token);

        assertEquals("patient-123", userId);
    }

    @Test
    void extractRole_shouldReturnRole() {

        String token = createToken("doctor-123", "DOCTOR");

        String role = jwtService.extractRole(token);

        assertEquals("DOCTOR", role);
    }

    @Test
    void extractAllClaims_shouldReturnClaims() {

        String token = createToken("patient-123", "PATIENT");

        Claims claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals("patient-123", claims.getSubject());
        assertEquals("PATIENT", claims.get("role", String.class));
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {

        String token = createToken("patient-123", "PATIENT");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {

        String token = "invalid.jwt.token";

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalseForTokenSignedWithDifferentSecret() {

        SecretKey differentKey = Keys.hmacShaKeyFor(
                "AnotherSecretKeyForTesting12345678901234567890"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("patient-123")
                .claim("role", "PATIENT")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60_000)
                )
                .signWith(differentKey)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    private String createToken(String userId, String role) {

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60_000)
                )
                .signWith(secretKey)
                .compact();
    }
}