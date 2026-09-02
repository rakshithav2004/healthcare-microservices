package com.healthcare.appointment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(
        String id,
        String userId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}