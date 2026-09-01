package com.healthcare.patient.controller;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody PatientRequest request) {

        PatientResponse response =
                patientService.createPatient(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.ok(
                patientService.getPatientByUserId(userId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<PatientResponse> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody PatientRequest request) {

        return ResponseEntity.ok(
                patientService.updatePatient(userId, request)
        );
    }
}