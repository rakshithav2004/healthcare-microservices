package com.healthcare.doctor.controller;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;
import com.healthcare.doctor.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(
            summary = "Create doctor profile",
            description = "Creates a profile for the authenticated doctor."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(
            Authentication authentication,
            @Valid @RequestBody DoctorRequest request) {

        String userId = authentication.getName();

        DoctorResponse response =
                doctorService.createDoctor(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get my profile",
            description = "Returns the profile of the authenticated doctor."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            Authentication authentication) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                doctorService.getDoctorByUserId(userId)
        );
    }

    @Operation(
            summary = "Update my profile",
            description = "Updates the profile of the authenticated doctor."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me")
    public ResponseEntity<DoctorResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody DoctorRequest request) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                doctorService.updateDoctor(userId, request)
        );
    }

    @Operation(
            summary = "Get doctor by ID",
            description = "Returns a doctor profile using the user ID."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{userId}")
    public ResponseEntity<DoctorResponse> getDoctorByUserId(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                doctorService.getDoctorByUserId(userId)
        );
    }
}