package com.healthcare.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthenticationEntryPointTest {

    private final CustomAuthenticationEntryPoint entryPoint =
            new CustomAuthenticationEntryPoint();

    @Test
    void commence_shouldReturn401Unauthorized() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AuthenticationException exception =
                new BadCredentialsException("Authentication failed");

        entryPoint.commence(request, response, exception);

        assertThat(response.getStatus())
                .isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);

        assertThat(response.getContentType())
                .contains("application/json");

        assertThat(response.getContentAsString())
                .contains("\"status\": 401");

        assertThat(response.getContentAsString())
                .contains("\"error\": \"Unauthorized\"");

        assertThat(response.getContentAsString())
                .contains("Authentication is required to access this resource");
    }
}