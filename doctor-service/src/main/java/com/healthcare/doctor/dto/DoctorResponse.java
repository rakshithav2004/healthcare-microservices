package com.healthcare.doctor.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {

  private String id;
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
