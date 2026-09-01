package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.exception.ResourceAlreadyExistsException;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.model.Patient;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientResponse createPatient(String userId, PatientRequest request) {

        if (patientRepository.existsByUserId(userId)) {
            throw new ResourceAlreadyExistsException("Patient profile already exists");
        }

        LocalDateTime now = LocalDateTime.now();

        Patient patient = Patient.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .address(request.getAddress())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Patient savedPatient = patientRepository.save(patient);

        return mapToResponse(savedPatient);
    }

    @Override
    public PatientResponse getPatientByUserId(String userId) {

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found"));

        return mapToResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(
            String userId,
            PatientRequest request) {

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found"));

        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setUpdatedAt(LocalDateTime.now());

        Patient updatedPatient = patientRepository.save(patient);

        return mapToResponse(updatedPatient);
    }

    private PatientResponse mapToResponse(Patient patient) {

        return PatientResponse.builder()
                .id(patient.getId())
                .userId(patient.getUserId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}