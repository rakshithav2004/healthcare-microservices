package com.healthcare.doctor.repository;

import com.healthcare.doctor.model.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;


    @Test
    void existsByUserId_shouldReturnTrue_whenDoctorExists() {

        Doctor doctor = createDoctor(
                "doctor-user-test",
                "user-doctor-test",
                "LIC-USER-TEST"
        );

        doctorRepository.save(doctor);

        boolean result =
                doctorRepository.existsByUserId("user-doctor-test");

        assertTrue(result);
    }


    @Test
    void existsByUserId_shouldReturnFalse_whenDoctorDoesNotExist() {

        boolean result =
                doctorRepository.existsByUserId("unknown-user");

        assertFalse(result);
    }


    @Test
    void findByUserId_shouldReturnDoctor_whenDoctorExists() {

        Doctor doctor = createDoctor(
                "doctor-find-test",
                "user-find-test",
                "LIC-FIND-TEST"
        );

        doctorRepository.save(doctor);

        Optional<Doctor> result =
                doctorRepository.findByUserId("user-find-test");

        assertTrue(result.isPresent());
        assertEquals(
                "user-find-test",
                result.get().getUserId()
        );
        assertEquals(
                "John",
                result.get().getFirstName()
        );
        assertEquals(
                "Cardiology",
                result.get().getSpecialization()
        );
    }


    @Test
    void findByUserId_shouldReturnEmpty_whenDoctorDoesNotExist() {

        Optional<Doctor> result =
                doctorRepository.findByUserId("unknown-user");

        assertTrue(result.isEmpty());
    }


    @Test
    void existsByLicenseNumber_shouldReturnTrue_whenLicenseExists() {

        Doctor doctor = createDoctor(
                "doctor-license-test",
                "user-license-test",
                "LIC-EXISTS-TEST"
        );

        doctorRepository.save(doctor);

        boolean result =
                doctorRepository.existsByLicenseNumber(
                        "LIC-EXISTS-TEST"
                );

        assertTrue(result);
    }


    @Test
    void existsByLicenseNumber_shouldReturnFalse_whenLicenseDoesNotExist() {

        boolean result =
                doctorRepository.existsByLicenseNumber(
                        "LIC-NOT-EXISTS"
                );

        assertFalse(result);
    }


    private Doctor createDoctor(
            String id,
            String userId,
            String licenseNumber) {

        return Doctor.builder()
                .id(id)
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .specialization("Cardiology")
                .qualification("MBBS")
                .licenseNumber(licenseNumber)
                .experienceYears(5)
                .phone("9876543210")
                .hospital("City Hospital")
                .address("Bangalore")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}