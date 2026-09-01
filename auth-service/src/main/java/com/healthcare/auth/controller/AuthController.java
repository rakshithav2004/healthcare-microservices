package com.healthcare.auth.controller;

import com.healthcare.auth.dto.AuthResponse;
import com.healthcare.auth.dto.LoginRequest;
import com.healthcare.auth.dto.RegisterRequest;
import com.healthcare.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {

        return ResponseEntity.ok(
                "User ID: " + authentication.getName()
                        + ", Authorities: " + authentication.getAuthorities()
        );
    }

    @GetMapping("/patient-test")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> patientTest() {
        return ResponseEntity.ok("Patient access granted");
    }
}