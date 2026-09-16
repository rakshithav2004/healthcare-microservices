package com.healthcare.patient.repository;

import com.healthcare.patient.model.Patient;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepository extends MongoRepository<Patient, String> {

  Optional<Patient> findByUserId(String userId);

  boolean existsByUserId(String userId);
}
