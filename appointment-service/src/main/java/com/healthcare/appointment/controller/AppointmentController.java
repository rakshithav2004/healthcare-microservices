package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Book an appointment",
            description = "Books an appointment with a doctor."
    )
    @PreAuthorize("hasRole('PATIENT')")
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

    @Operation(
            summary = "Get appointment by ID",
            description = "Returns an appointment if the authenticated user is the patient or doctor assigned to it."
    )
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

    @Operation(
            summary = "Get my appointments",
            description = "Returns all appointments of the authenticated patient."
    )
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/me")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            Authentication authentication) {

        String patientId = authentication.getName();

        return ResponseEntity.ok(
                appointmentService.getPatientAppointments(patientId)
        );
    }

    @Operation(
            summary = "Get doctor appointments",
            description = "Returns all appointments assigned to the authenticated doctor."
    )
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            Authentication authentication,
            @PathVariable String doctorId) {

        String authenticatedDoctorId = authentication.getName();

        if (!authenticatedDoctorId.equals(doctorId)) {
            throw new IllegalStateException(
                    "You are not authorized to view these appointments"
            );
        }

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(doctorId)
        );
    }

    @Operation(
            summary = "Cancel appointment",
            description = "Cancels an appointment booked by the patient."
    )
    @PreAuthorize("hasRole('PATIENT')")
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

    @Operation(
            summary = "Confirm appointment",
            description = "Confirms a booked appointment as a doctor."
    )
    @PreAuthorize("hasRole('DOCTOR')")
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

    @Operation(
            summary = "Complete appointment",
            description = "Marks a confirmed appointment as completed."
    )
    @PreAuthorize("hasRole('DOCTOR')")
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