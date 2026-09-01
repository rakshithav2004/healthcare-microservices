package com.healthcare.doctor.repository;

import com.healthcare.doctor.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DoctorRepository extends MongoRepository<Doctor, String> {

    boolean existsByUserId(String userId);

    Optional<Doctor> findByUserId(String userId);

    boolean existsByLicenseNumber(String licenseNumber);
}