package com.healthcare.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
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

    @Test
    void validToken_shouldSetAuthentication()
            throws ServletException, java.io.IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        when(jwtService.extractUserId("valid-token"))
                .thenReturn("user-123");

        when(jwtService.extractRole("valid-token"))
                .thenReturn("PATIENT");

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication())
                .isNotNull();

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName())
                .isEqualTo("user-123");

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PATIENT");

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void noAuthorizationHeader_shouldNotSetAuthentication()
            throws ServletException, java.io.IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication())
                .isNull();

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void invalidToken_shouldClearAuthentication()
            throws ServletException, java.io.IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        when(jwtService.extractUserId("invalid-token"))
                .thenThrow(new RuntimeException("Invalid token"));

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication())
                .isNull();

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void nonBearerAuthorizationHeader_shouldNotAuthenticate()
            throws ServletException, java.io.IOException {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        request.addHeader(
                "Authorization",
                "Basic username:password"
        );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(SecurityContextHolder
                .getContext()
                .getAuthentication())
                .isNull();

        verify(filterChain)
                .doFilter(request, response);
    }
}