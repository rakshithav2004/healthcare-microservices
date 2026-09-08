package com.healthcare.patient.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {

        jwtService = mock(JwtService.class);

        filter = new JwtAuthenticationFilter(jwtService);

        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_shouldSetAuthentication() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        when(jwtService.isTokenValid("valid-token"))
                .thenReturn(true);

        when(jwtService.extractUserId("valid-token"))
                .thenReturn("user-123");

        when(jwtService.extractRole("valid-token"))
                .thenReturn("PATIENT");

        filter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(authentication)
                .isNotNull();

        assertThat(authentication.getName())
                .isEqualTo("user-123");

        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PATIENT");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void invalidToken_shouldNotSetAuthentication()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        when(jwtService.isTokenValid("invalid-token"))
                .thenReturn(false);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(authentication)
                .isNull();

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void missingAuthorizationHeader_shouldNotSetAuthentication()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void nonBearerAuthorizationHeader_shouldNotSetAuthentication()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Basic abc123"
        );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(jwtService);
    }

    @Test
    void tokenProcessingException_shouldContinueFilterChain()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer broken-token"
        );

        when(jwtService.isTokenValid("broken-token"))
                .thenThrow(new RuntimeException("JWT error"));

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(filterChain)
                .doFilter(request, response);
    }
}