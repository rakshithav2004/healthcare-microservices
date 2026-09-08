package com.healthcare.doctor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {

        jwtService = Mockito.mock(JwtService.class);

        filter = new JwtAuthenticationFilter(jwtService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_shouldAuthenticate_whenValidBearerToken()
            throws ServletException, IOException {

        String token = "valid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(true);

        when(jwtService.extractUserId(token))
                .thenReturn("user-123");

        when(jwtService.extractRole(token))
                .thenReturn("DOCTOR");

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                "user-123",
                authentication.getName()
        );

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_DOCTOR")
                        )
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticate_whenAuthorizationHeaderMissing()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticate_whenHeaderIsNotBearer()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticate_whenTokenIsInvalid()
            throws ServletException, IOException {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void doFilter_shouldNotAuthenticate_whenJwtServiceThrowsException()
            throws ServletException, IOException {

        String token = "bad-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtService.isTokenValid(token))
                .thenThrow(new RuntimeException("JWT error"));

        filter.doFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);
    }
}