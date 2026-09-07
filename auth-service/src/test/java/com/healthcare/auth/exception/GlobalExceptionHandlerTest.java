package com.healthcare.auth.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleIllegalState_shouldReturnConflict() {

        IllegalStateException exception =
                new IllegalStateException("User already exists");

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalState(exception);

        assertThat(response.getStatusCode().value())
                .isEqualTo(HttpStatus.CONFLICT.value());

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().error())
                .isEqualTo("Conflict");

        assertThat(response.getBody().message())
                .isEqualTo("User already exists");
    }

    @Test
    void handleGeneralException_shouldReturnInternalServerError() {

        Exception exception =
                new Exception("Database connection failed");

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneralException(exception);

        assertThat(response.getStatusCode().value())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

        assertThat(response.getBody())
                .isNotNull();

        assertThat(response.getBody().error())
                .isEqualTo("Internal Server Error");

        assertThat(response.getBody().message())
                .isEqualTo("An unexpected error occurred");
    }
}