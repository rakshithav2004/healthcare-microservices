package com.healthcare.appointment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    private String id;

    @Indexed
    private String patientId;

    @Indexed
    private String doctorId;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String reason;

    private AppointmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}