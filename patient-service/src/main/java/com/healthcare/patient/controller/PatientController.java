package com.healthcare.patient.controller;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;

    @Operation(
            summary = "Create patient profile",
            description = "Creates a profile for the authenticated patient."
    )
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            Authentication authentication,
            @Valid @RequestBody PatientRequest request) {

        String userId = authentication.getName();

        PatientResponse response =
                patientService.createPatient(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get my profile",
            description = "Returns the profile of the authenticated patient."
    )
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile(
            Authentication authentication) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                patientService.getPatientByUserId(userId)
        );
    }

    @Operation(
            summary = "Update my profile",
            description = "Updates the profile of the authenticated patient."
    )
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody PatientRequest request) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                patientService.updatePatient(userId, request)
        );
    }

    @Operation(
            summary = "Get patient by user ID",
            description = "Returns a patient profile using the user ID."
    )
    @GetMapping("/{userId}")
    public ResponseEntity<PatientResponse> getPatientByUserId(
            Authentication authentication,
            @PathVariable String userId) {

        String authenticatedUserId = authentication.getName();

        if (!authenticatedUserId.equals(userId)) {
            throw new IllegalStateException(
                    "You are not authorized to view this patient profile"
            );
        }

        return ResponseEntity.ok(
                patientService.getPatientByUserId(userId)
        );
    }
}