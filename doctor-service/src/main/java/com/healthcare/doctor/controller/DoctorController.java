package com.healthcare.doctor.controller;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;
import com.healthcare.doctor.service.DoctorService;
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

    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(
            Authentication authentication) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                doctorService.getDoctorByUserId(userId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<DoctorResponse> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody DoctorRequest request) {

        String userId = authentication.getName();

        return ResponseEntity.ok(
                doctorService.updateDoctor(userId, request)
        );
    }
}