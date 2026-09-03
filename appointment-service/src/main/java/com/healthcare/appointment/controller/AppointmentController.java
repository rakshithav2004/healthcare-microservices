package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentRequest request) {

        String patientId = authentication.getName();

        AppointmentResponse response =
                appointmentService.bookAppointment(patientId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponse> getAppointment(
            Authentication authentication,
            @PathVariable String appointmentId) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(
                        appointmentId,
                        userId
                )
        );
    }

    @GetMapping("/patient/me")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            Authentication authentication) {

        String patientId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.getPatientAppointments(patientId)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @PathVariable String doctorId) {

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(doctorId)
        );
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            Authentication authentication,
            @PathVariable String appointmentId) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.cancelAppointment(
                        appointmentId,
                        userId
                )
        );
    }

    @PutMapping("/{appointmentId}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            Authentication authentication,
            @PathVariable String appointmentId) {

        String doctorId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.confirmAppointment(
                        appointmentId,
                        doctorId
                )
        );
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            Authentication authentication,
            @PathVariable String appointmentId) {

        String doctorId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.completeAppointment(
                        appointmentId,
                        doctorId
                )
        );
    }
}