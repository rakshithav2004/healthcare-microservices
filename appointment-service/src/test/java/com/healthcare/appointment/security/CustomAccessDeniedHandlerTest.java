package com.healthcare.appointment.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAccessDeniedHandlerTest {

    @Test
    void handle_shouldReturn403ForbiddenResponse() throws Exception {

        CustomAccessDeniedHandler handler =
                new CustomAccessDeniedHandler();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        HttpServletResponse response =
                mock(HttpServletResponse.class);

        AccessDeniedException exception =
                new AccessDeniedException("Access denied");

        StringWriter stringWriter = new StringWriter();

        when(response.getWriter())
                .thenReturn(new PrintWriter(stringWriter));

        handler.handle(request, response, exception);

        verify(response).setStatus(
                HttpServletResponse.SC_FORBIDDEN
        );

        verify(response).setContentType(
                "application/json"
        );

        String json = stringWriter.toString();

        assertFalse(json.isBlank());

        JsonNode responseJson =
                new ObjectMapper().readTree(json);

        assertEquals(
                "Forbidden",
                responseJson.get("error").asText()
        );

        assertEquals(
                "You are not authorized to perform this action",
                responseJson.get("message").asText()
        );

        assertEquals(
                403,
                responseJson.get("status").asInt()
        );
    }
}