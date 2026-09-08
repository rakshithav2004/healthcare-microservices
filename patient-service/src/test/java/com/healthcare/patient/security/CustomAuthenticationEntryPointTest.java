package com.healthcare.patient.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthenticationEntryPointTest {

    @Test
    void commence_shouldReturn401Unauthorized() throws Exception {

        CustomAuthenticationEntryPoint entryPoint =
                new CustomAuthenticationEntryPoint();

        HttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        entryPoint.commence(
                request,
                response,
                new BadCredentialsException("Invalid credentials")
        );

        assertThat(response.getStatus()).isEqualTo(401);

        assertThat(response.getContentType())
                .contains("application/json");

        String content = response.getContentAsString();

        assertThat(content)
                .contains("\"status\": 401");

        assertThat(content)
                .contains("\"error\": \"Unauthorized\"");

        assertThat(content)
                .contains(
                        "\"message\": \"Authentication is required to access this resource\""
                );

        assertThat(content)
                .contains("\"timestamp\"");
    }
}