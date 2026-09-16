package com.healthcare.doctor.repository;

import com.healthcare.doctor.model.Doctor;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DoctorRepository extends MongoRepository<Doctor, String> {

  boolean existsByUserId(String userId);

  Optional<Doctor> findByUserId(String userId);

  boolean existsByLicenseNumber(String licenseNumber);
}
