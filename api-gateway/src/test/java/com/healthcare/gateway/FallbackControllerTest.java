package com.healthcare.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FallbackControllerTest {

    private final FallbackController controller =
            new FallbackController();

    @Test
    void shouldReturn503WhenDoctorServiceIsUnavailable() {

        ResponseEntity<Map<String, Object>> response =
                controller.fallback("Doctor Service");

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
    void shouldReturn503WhenPatientServiceIsUnavailable() {

        ResponseEntity<Map<String, Object>> response =
                controller.fallback("Patient Service");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Patient Service is currently unavailable",
                response.getBody().get("message")
        );

        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldUseDefaultServiceNameWhenNoServiceProvided() {

        ResponseEntity<Map<String, Object>> response =
                controller.fallback("Service");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(503, response.getBody().get("status"));
        assertEquals(
                "Service is currently unavailable",
                response.getBody().get("message")
        );

        assertNotNull(response.getBody().get("timestamp"));
    }
}