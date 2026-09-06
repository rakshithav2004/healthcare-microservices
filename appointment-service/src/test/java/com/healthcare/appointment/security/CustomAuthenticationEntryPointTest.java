package com.healthcare.appointment.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    @Test
    void commence_shouldReturn401UnauthorizedResponse() throws Exception {

        CustomAuthenticationEntryPoint entryPoint =
                new CustomAuthenticationEntryPoint();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        HttpServletResponse response =
                mock(HttpServletResponse.class);

        BadCredentialsException exception =
                new BadCredentialsException("Invalid token");

        StringWriter stringWriter = new StringWriter();

        when(response.getWriter())
                .thenReturn(new PrintWriter(stringWriter));

        entryPoint.commence(request, response, exception);

        verify(response).setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        verify(response).setContentType(
                "application/json"
        );

        String json = stringWriter.toString();

        assertFalse(json.isBlank());

        JsonNode responseJson =
                new ObjectMapper().readTree(json);

        assertEquals(
                "Unauthorized",
                responseJson.get("error").asText()
        );

        assertEquals(
                "Authentication is required to access this resource",
                responseJson.get("message").asText()
        );

        assertEquals(
                401,
                responseJson.get("status").asInt()
        );
    }
}