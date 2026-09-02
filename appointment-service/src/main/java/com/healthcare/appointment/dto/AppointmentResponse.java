package com.healthcare.appointment.dto;

import com.healthcare.appointment.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(
        String id,
        String patientId,
        String doctorId,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String reason,
        AppointmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}