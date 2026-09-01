package com.healthcare.doctor.service;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;

public interface DoctorService {

    DoctorResponse createDoctor(String userId, DoctorRequest request);

    DoctorResponse getDoctorByUserId(String userId);

    DoctorResponse updateDoctor(String userId, DoctorRequest request);
}