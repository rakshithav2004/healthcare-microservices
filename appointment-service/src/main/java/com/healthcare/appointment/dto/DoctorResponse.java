package com.healthcare.appointment.dto;

import java.time.LocalDateTime;

public record DoctorResponse(
        String id,
        String userId,
        String firstName,
        String lastName,
        String specialization,
        String qualification,
        String licenseNumber,
        Integer experienceYears,
        String phone,
        String hospital,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}