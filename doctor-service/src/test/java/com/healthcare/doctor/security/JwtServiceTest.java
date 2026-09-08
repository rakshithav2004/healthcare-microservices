package com.healthcare.doctor.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(secret);

        secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {

        String token = createToken("user-123", "DOCTOR");

        String userId = jwtService.extractUserId(token);

        assertEquals("user-123", userId);
    }

    @Test
    void extractRole_shouldReturnCorrectRole() {

        String token = createToken("user-123", "DOCTOR");

        String role = jwtService.extractRole(token);

        assertEquals("DOCTOR", role);
    }

    @Test
    void extractAllClaims_shouldReturnClaims() {

        String token = createToken("user-123", "DOCTOR");

        var claims = jwtService.extractAllClaims(token);

        assertNotNull(claims);
        assertEquals("user-123", claims.getSubject());
        assertEquals("DOCTOR", claims.get("role", String.class));
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {

        String token = createToken("user-123", "DOCTOR");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {

        String invalidToken = "invalid.jwt.token";

        assertFalse(jwtService.isTokenValid(invalidToken));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forTokenSignedWithDifferentKey() {

        SecretKey differentKey = Keys.hmacShaKeyFor(
                "AnotherSecretKeyForTestingHealthcareService2026Secure"
                        .getBytes(StandardCharsets.UTF_8)
        );

        String token = Jwts.builder()
                .subject("user-123")
                .claim("role", "DOCTOR")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60000)
                )
                .signWith(differentKey)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() {

        String token = Jwts.builder()
                .subject("user-123")
                .claim("role", "DOCTOR")
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    private String createToken(String userId, String role) {

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + 60000)
                )
                .signWith(secretKey)
                .compact();
    }
}