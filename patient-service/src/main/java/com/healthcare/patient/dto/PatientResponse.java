package com.healthcare.patient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
  private String id;
  private String userId;
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private String gender;
  private String phone;
  private String address;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
