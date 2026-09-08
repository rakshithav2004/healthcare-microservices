package com.healthcare.patient.repository;

import com.healthcare.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
    }

    @Test
    void findByUserId_shouldReturnPatient() {

        Patient patient = Patient.builder()
                .userId("user-123")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .phone("9876543210")
                .address("Bangalore")
                .build();

        patientRepository.save(patient);

        var result = patientRepository.findByUserId("user-123");

        assertThat(result).isPresent();
        assertThat(result.get().getUserId())
                .isEqualTo("user-123");
        assertThat(result.get().getFirstName())
                .isEqualTo("John");
        assertThat(result.get().getLastName())
                .isEqualTo("Doe");
    }

    @Test
    void findByUserId_shouldReturnEmptyForUnknownUser() {

        var result =
                patientRepository.findByUserId("unknown-user");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUserId_shouldReturnTrueForExistingPatient() {

        Patient patient = Patient.builder()
                .userId("user-456")
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1998, 8, 15))
                .gender("FEMALE")
                .phone("9876543211")
                .address("Bangalore")
                .build();

        patientRepository.save(patient);

        boolean exists =
                patientRepository.existsByUserId("user-456");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUserId_shouldReturnFalseForUnknownUser() {

        boolean exists =
                patientRepository.existsByUserId("unknown-user");

        assertThat(exists).isFalse();
    }
}