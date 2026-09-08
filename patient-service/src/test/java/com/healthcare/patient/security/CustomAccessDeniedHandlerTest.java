package com.healthcare.patient.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAccessDeniedHandlerTest {

    @Test
    void handle_shouldReturn403Forbidden() throws Exception {

        CustomAccessDeniedHandler handler =
                new CustomAccessDeniedHandler();

        HttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        handler.handle(
                request,
                response,
                new AccessDeniedException("Access denied")
        );

        assertThat(response.getStatus())
                .isEqualTo(403);

        assertThat(response.getContentType())
                .contains("application/json");

        String content =
                response.getContentAsString();

        assertThat(content)
                .contains("\"status\": 403");

        assertThat(content)
                .contains("\"error\": \"Forbidden\"");

        assertThat(content)
                .contains(
                        "\"message\": \"You do not have permission to access this resource\""
                );

        assertThat(content)
                .contains("\"timestamp\"");
    }
}