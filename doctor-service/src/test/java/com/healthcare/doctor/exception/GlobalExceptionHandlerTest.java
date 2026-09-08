package com.healthcare.doctor.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler =
            new GlobalExceptionHandler();


    @Test
    void handleResourceNotFound_shouldReturn404() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Doctor profile not found"
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleResourceNotFound(exception);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                404,
                response.getBody().status()
        );

        assertEquals(
                "Not Found",
                response.getBody().error()
        );

        assertEquals(
                "Doctor profile not found",
                response.getBody().message()
        );

        assertNotNull(response.getBody().timestamp());
    }


    @Test
    void handleIllegalState_shouldReturn409() {

        IllegalStateException exception =
                new IllegalStateException(
                        "Doctor profile already exists"
                );

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleIllegalState(exception);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                409,
                response.getBody().status()
        );

        assertEquals(
                "Conflict",
                response.getBody().error()
        );

        assertEquals(
                "Doctor profile already exists",
                response.getBody().message()
        );

        assertNotNull(response.getBody().timestamp());
    }


    @Test
    void handleValidation_shouldReturn400() {

        MethodArgumentNotValidException exception =
                createValidationException();

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidation(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                400,
                response.getBody().status()
        );

        assertEquals(
                "Validation Failed",
                response.getBody().error()
        );

        assertNotNull(response.getBody().message());

        assertTrue(
                response.getBody().message() instanceof java.util.Map
        );

        @SuppressWarnings("unchecked")
        java.util.Map<String, String> errors =
                (java.util.Map<String, String>)
                        response.getBody().message();

        assertEquals(
                "First name is required",
                errors.get("firstName")
        );

        assertEquals(
                "Phone number is invalid",
                errors.get("phone")
        );

        assertNotNull(response.getBody().timestamp());
    }


    @Test
    void handleGeneralException_shouldReturn500() {

        Exception exception =
                new Exception("Database connection failed");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleGeneralException(exception);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                500,
                response.getBody().status()
        );

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


    private MethodArgumentNotValidException createValidationException() {

        BindingResult bindingResult =
                org.mockito.Mockito.mock(BindingResult.class);

        FieldError firstNameError =
                new FieldError(
                        "doctorRequest",
                        "firstName",
                        "First name is required"
                );

        FieldError phoneError =
                new FieldError(
                        "doctorRequest",
                        "phone",
                        "Phone number is invalid"
                );

        org.mockito.Mockito.when(
                bindingResult.getFieldErrors()
        ).thenReturn(
                List.of(
                        firstNameError,
                        phoneError
                )
        );

        return new MethodArgumentNotValidException(
                null,
                bindingResult
        );
    }
}