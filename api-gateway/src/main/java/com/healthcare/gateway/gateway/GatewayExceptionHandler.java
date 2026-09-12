package com.healthcare.gateway.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpServerError(
            HttpServerErrorException ex) {

        String message = "Service is currently unavailable";

        String errorMessage = ex.getMessage();

        if (errorMessage != null) {
            if (errorMessage.contains("DOCTOR-SERVICE")) {
                message = "Doctor Service is currently unavailable";
            } else if (errorMessage.contains("PATIENT-SERVICE")) {
                message = "Patient Service is currently unavailable";
            } else if (errorMessage.contains("AUTH-SERVICE")) {
                message = "Auth Service is currently unavailable";
            } else if (errorMessage.contains("APPOINTMENT-SERVICE")) {
                message = "Appointment Service is currently unavailable";
            }
        }

        Map<String, Object> response = Map.of(
                "status", 503,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}