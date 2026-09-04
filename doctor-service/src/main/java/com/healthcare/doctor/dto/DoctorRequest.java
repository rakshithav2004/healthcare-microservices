package com.healthcare.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "First name can contain only letters and spaces"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Last name can contain only letters and spaces"
    )
    private String lastName;

    @NotBlank(message = "Specialization is required")
    @Size(min = 2, max = 100, message = "Specialization must be between 2 and 100 characters")
    private String specialization;

    @NotBlank(message = "Qualification is required")
    @Size(min = 2, max = 100, message = "Qualification must be between 2 and 100 characters")
    private String qualification;

    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 50, message = "License number must be between 5 and 50 characters")
    private String licenseNumber;

    @NotNull(message = "Experience years is required")
    @PositiveOrZero(message = "Experience years cannot be negative")
    private Integer experienceYears;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    private String phone;

    @Size(max = 150, message = "Hospital name cannot exceed 150 characters")
    private String hospital;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;
}