package com.healthcare.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAccessDeniedHandlerTest {

    private final CustomAccessDeniedHandler handler =
            new CustomAccessDeniedHandler();

    @Test
    void handle_shouldReturn403Forbidden() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        AccessDeniedException exception =
                new AccessDeniedException("Access denied");

        handler.handle(request, response, exception);

        assertThat(response.getStatus())
                .isEqualTo(HttpServletResponse.SC_FORBIDDEN);

        assertThat(response.getContentType())
                .contains("application/json");

        assertThat(response.getContentAsString())
                .contains("\"status\": 403");

        assertThat(response.getContentAsString())
                .contains("\"error\": \"Forbidden\"");

        assertThat(response.getContentAsString())
                .contains("You do not have permission to access this resource");
    }
}