package com.healthcare.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GatewayExceptionHandlerTest {

    private final GatewayExceptionHandler handler =
            new GatewayExceptionHandler();

    @Test
    void shouldReturn503ForDoctorServiceUnavailable() {

        HttpServerErrorException exception =
                HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Unable to find instance for DOCTOR-SERVICE",
                        null,
                        null,
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpServerError(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Doctor Service is currently unavailable",
                response.getBody().get("message")
        );

        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldReturn503ForPatientServiceUnavailable() {

        HttpServerErrorException exception =
                HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Unable to find instance for PATIENT-SERVICE",
                        null,
                        null,
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpServerError(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Patient Service is currently unavailable",
                response.getBody().get("message")
        );
    }

    @Test
    void shouldReturn503ForAuthServiceUnavailable() {

        HttpServerErrorException exception =
                HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Unable to find instance for AUTH-SERVICE",
                        null,
                        null,
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpServerError(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Auth Service is currently unavailable",
                response.getBody().get("message")
        );
    }

    @Test
    void shouldReturn503ForAppointmentServiceUnavailable() {

        HttpServerErrorException exception =
                HttpServerErrorException.create(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Unable to find instance for APPOINTMENT-SERVICE",
                        null,
                        null,
                        null
                );

        ResponseEntity<Map<String, Object>> response =
                handler.handleHttpServerError(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Appointment Service is currently unavailable",
                response.getBody().get("message")
        );
    }
}