package com.healthcare.patient.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_shouldReturn404() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Patient profile not found"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status())
                .isEqualTo(404);
        assertThat(response.getBody().error())
                .isEqualTo("Not Found");
        assertThat(response.getBody().message())
                .isEqualTo("Patient profile not found");
        assertThat(response.getBody().timestamp())
                .isNotNull();
    }

    @Test
    void handleResourceAlreadyExists_shouldReturn409() {

        ResourceAlreadyExistsException exception =
                new ResourceAlreadyExistsException(
                        "Patient profile already exists"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceAlreadyExists(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status())
                .isEqualTo(409);
        assertThat(response.getBody().error())
                .isEqualTo("Conflict");
        assertThat(response.getBody().message())
                .isEqualTo("Patient profile already exists");
        assertThat(response.getBody().timestamp())
                .isNotNull();
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors()
            throws Exception {

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                mock(BindingResult.class);

        FieldError firstNameError =
                new FieldError(
                        "patientRequest",
                        "firstName",
                        "First name is required"
                );

        FieldError phoneError =
                new FieldError(
                        "patientRequest",
                        "phone",
                        "Phone number must be valid"
                );

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(
                        firstNameError,
                        phoneError
                ));

        ResponseEntity<ErrorResponse> response =
                handler.handleValidation(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status())
                .isEqualTo(400);
        assertThat(response.getBody().error())
                .isEqualTo("Validation Failed");

        assertThat(response.getBody().message())
                .isInstanceOf(java.util.Map.class);

        @SuppressWarnings("unchecked")
        var fieldErrors =
                (java.util.Map<String, String>)
                        response.getBody().message();

        assertThat(fieldErrors)
                .containsEntry(
                        "firstName",
                        "First name is required"
                );

        assertThat(fieldErrors)
                .containsEntry(
                        "phone",
                        "Phone number must be valid"
                );
    }

    @Test
    void handleGeneralException_shouldReturn500() {

        Exception exception =
                new RuntimeException("Database connection failed");

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneralException(exception);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status())
                .isEqualTo(500);
        assertThat(response.getBody().error())
                .isEqualTo("Internal Server Error");

        assertThat(response.getBody().message())
                .isEqualTo("An unexpected error occurred");

        assertThat(response.getBody().message())
                .isNotEqualTo("Database connection failed");

        assertThat(response.getBody().timestamp())
                .isNotNull();
    }
}