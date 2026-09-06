package com.healthcare.appointment.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException("Appointment not found");

        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Appointment not found", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void handleIllegalState_shouldReturn403ForUnauthorized() {

        IllegalStateException exception =
                new IllegalStateException(
                        "You are not authorized to perform this action"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalState(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(403, response.getBody().status());
        assertEquals("Forbidden", response.getBody().error());
        assertEquals(
                "You are not authorized to perform this action",
                response.getBody().message()
        );
    }

    @Test
    void handleIllegalState_shouldReturn409ForConflict() {

        IllegalStateException exception =
                new IllegalStateException("Appointment slot already booked");

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalState(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertEquals(
                "Appointment slot already booked",
                response.getBody().message()
        );
    }

    @Test
    void handleValidation_shouldReturn400() {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        var bindingResult =
                mock(org.springframework.validation.BindingResult.class);

        var fieldError =
                new org.springframework.validation.FieldError(
                        "appointmentRequest",
                        "doctorId",
                        "Doctor ID is required"
                );

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(java.util.List.of(fieldError));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(400, response.getBody().status());
        assertEquals("Validation Failed", response.getBody().error());

        assertTrue(response.getBody().message() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) response.getBody().message();

        assertEquals(
                "Doctor ID is required",
                errors.get("doctorId")
        );
    }

    @Test
    void handleGeneral_shouldReturn500() {

        Exception exception =
                new Exception("Database connection failed");

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneral(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(500, response.getBody().status());
        assertEquals(
                "Internal Server Error",
                response.getBody().error()
        );

        assertEquals(
                "An unexpected error occurred",
                response.getBody().message()
        );

        assertNotNull(response.getBody().timestamp());
    }
}