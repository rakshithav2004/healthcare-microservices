package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;

public interface PatientService {

    PatientResponse createPatient(String userId, PatientRequest request);

    PatientResponse getPatientByUserId(String userId);

    PatientResponse updatePatient(String userId, PatientRequest request);
}