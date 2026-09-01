package com.healthcare.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "Experience years is required")
    @PositiveOrZero(message = "Experience years cannot be negative")
    private Integer experienceYears;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String hospital;

    private String address;
}