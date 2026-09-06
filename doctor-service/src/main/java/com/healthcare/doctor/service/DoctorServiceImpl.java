package com.healthcare.doctor.service;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;
import com.healthcare.doctor.exception.ResourceNotFoundException;
import com.healthcare.doctor.model.Doctor;
import com.healthcare.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public DoctorResponse createDoctor(
            String userId,
            DoctorRequest request) {

        log.info(
                "Creating doctor profile: userId={}, specialization={}",
                userId,
                request.getSpecialization()
        );

        if (doctorRepository.existsByUserId(userId)) {

            log.warn(
                    "Doctor profile already exists: userId={}",
                    userId
            );

            throw new IllegalStateException(
                    "Doctor profile already exists"
            );
        }

        if (doctorRepository.existsByLicenseNumber(
                request.getLicenseNumber())) {

            log.warn(
                    "License number already exists: userId={}",
                    userId
            );

            throw new IllegalStateException(
                    "License number already exists"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Doctor doctor = Doctor.builder()
                .userId(userId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears())
                .phone(request.getPhone())
                .hospital(request.getHospital())
                .address(request.getAddress())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        log.info(
                "Doctor profile created successfully: doctorId={}, userId={}",
                savedDoctor.getId(),
                userId
        );

        return mapToResponse(savedDoctor);
    }

    @Override
    public DoctorResponse getDoctorByUserId(String userId) {

        log.info(
                "Fetching doctor profile: userId={}",
                userId
        );

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> {

                    log.warn(
                            "Doctor profile not found: userId={}",
                            userId
                    );

                    return new ResourceNotFoundException(
                            "Doctor profile not found"
                    );
                });

        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctor(
            String userId,
            DoctorRequest request) {

        log.info(
                "Updating doctor profile: userId={}",
                userId
        );

        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> {

                    log.warn(
                            "Doctor profile not found for update: userId={}",
                            userId
                    );

                    return new ResourceNotFoundException(
                            "Doctor profile not found"
                    );
                });

        if (!doctor.getLicenseNumber()
                .equals(request.getLicenseNumber())
                && doctorRepository.existsByLicenseNumber(
                request.getLicenseNumber())) {

            log.warn(
                    "License number already exists during update: userId={}",
                    userId
            );

            throw new IllegalStateException(
                    "License number already exists"
            );
        }

        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setQualification(request.getQualification());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setPhone(request.getPhone());
        doctor.setHospital(request.getHospital());
        doctor.setAddress(request.getAddress());
        doctor.setUpdatedAt(LocalDateTime.now());

        Doctor updatedDoctor = doctorRepository.save(doctor);

        log.info(
                "Doctor profile updated successfully: userId={}",
                userId
        );

        return mapToResponse(updatedDoctor);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {

        return DoctorResponse.builder()
                .id(doctor.getId())
                .userId(doctor.getUserId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .specialization(doctor.getSpecialization())
                .qualification(doctor.getQualification())
                .licenseNumber(doctor.getLicenseNumber())
                .experienceYears(doctor.getExperienceYears())
                .phone(doctor.getPhone())
                .hospital(doctor.getHospital())
                .address(doctor.getAddress())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
}