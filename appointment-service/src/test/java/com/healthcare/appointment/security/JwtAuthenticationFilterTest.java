package com.healthcare.appointment.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;

    private final String secret =
            "HealthcareMicroservicesJwtSecret2026SecureKeyForDevelopmentOnly123456";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret);
        filter = new JwtAuthenticationFilter(jwtService);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldAuthenticateValidToken()
            throws Exception {

        String token = createToken("patient-123", "PATIENT");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("patient-123", authentication.getName());
        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_PATIENT")
                        )
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldAuthenticateDoctorToken()
            throws Exception {

        String token = createToken("doctor-123", "DOCTOR");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("doctor-123", authentication.getName());

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_DOCTOR")
                        )
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldContinueWithoutAuthenticationWhenHeaderMissing()
            throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldContinueWithoutAuthenticationForInvalidToken()
            throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer invalid-token");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldIgnoreNonBearerHeader()
            throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Basic some-token");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain).doFilter(request, response);
    }

    private String createToken(String userId, String role) {

        return io.jsonwebtoken.Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new java.util.Date())
                .expiration(
                        new java.util.Date(
                                System.currentTimeMillis() + 60_000
                        )
                )
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                secret.getBytes(
                                        java.nio.charset.StandardCharsets.UTF_8
                                )
                        )
                )
                .compact();
    }
}