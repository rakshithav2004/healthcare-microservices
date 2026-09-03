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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Book an appointment",
            description = "Books an appointment with a doctor."
    )
    @SecurityRequirement(name = "bearerAuth")
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
            description = "Returns an appointment using its ID."
    )
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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
            description = "Returns all appointments assigned to a doctor."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @PathVariable String doctorId) {

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(doctorId)
        );
    }

    @Operation(
            summary = "Cancel appointment",
            description = "Cancels an appointment booked by the patient."
    )
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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
    @SecurityRequirement(name = "bearerAuth")
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