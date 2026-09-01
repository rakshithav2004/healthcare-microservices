package com.healthcare.doctor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "doctors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String firstName;
    private String lastName;
    private String specialization;
    private String qualification;
    private String licenseNumber;
    private Integer experienceYears;
    private String phone;
    private String hospital;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}