package com.healthcare.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequest(

        @NotBlank(message = "Doctor ID is required")
        String doctorId,

        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date must be today or in the future")
        LocalDate appointmentDate,

        @NotNull(message = "Appointment time is required")
        LocalTime appointmentTime,

        @NotBlank(message = "Reason is required")
        @Size(min = 5, max = 300, message = "Reason must be between 5 and 300 characters")
        String reason
) {
}