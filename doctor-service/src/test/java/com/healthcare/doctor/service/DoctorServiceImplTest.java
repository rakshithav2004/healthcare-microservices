package com.healthcare.doctor.service;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;
import com.healthcare.doctor.exception.ResourceNotFoundException;
import com.healthcare.doctor.model.Doctor;
import com.healthcare.doctor.repository.DoctorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private DoctorRequest request;
    private Doctor doctor;

    @BeforeEach
    void setUp() {

        request = new DoctorRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialization("Cardiology");
        request.setQualification("MBBS");
        request.setLicenseNumber("LIC-12345");
        request.setExperienceYears(5);
        request.setPhone("9876543210");
        request.setHospital("City Hospital");
        request.setAddress("Bangalore");

        doctor = Doctor.builder()
                .id("doctor-123")
                .userId("user-123")
                .firstName("John")
                .lastName("Doe")
                .specialization("Cardiology")
                .qualification("MBBS")
                .licenseNumber("LIC-12345")
                .experienceYears(5)
                .phone("9876543210")
                .hospital("City Hospital")
                .address("Bangalore")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    @Test
    void createDoctor_shouldCreateSuccessfully() {

        when(doctorRepository.existsByUserId("user-123"))
                .thenReturn(false);

        when(doctorRepository.existsByLicenseNumber("LIC-12345"))
                .thenReturn(false);

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponse response =
                doctorService.createDoctor("user-123", request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("doctor-123");
        assertThat(response.getUserId()).isEqualTo("user-123");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getSpecialization())
                .isEqualTo("Cardiology");
        assertThat(response.getLicenseNumber())
                .isEqualTo("LIC-12345");

        verify(doctorRepository)
                .existsByUserId("user-123");

        verify(doctorRepository)
                .existsByLicenseNumber("LIC-12345");

        verify(doctorRepository)
                .save(any(Doctor.class));
    }


    @Test
    void createDoctor_shouldThrowExceptionWhenUserAlreadyHasProfile() {

        when(doctorRepository.existsByUserId("user-123"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                doctorService.createDoctor("user-123", request)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Doctor profile already exists");

        verify(doctorRepository)
                .existsByUserId("user-123");

        verify(doctorRepository, never())
                .existsByLicenseNumber(anyString());

        verify(doctorRepository, never())
                .save(any(Doctor.class));
    }


    @Test
    void createDoctor_shouldThrowExceptionWhenLicenseAlreadyExists() {

        when(doctorRepository.existsByUserId("user-123"))
                .thenReturn(false);

        when(doctorRepository.existsByLicenseNumber("LIC-12345"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                doctorService.createDoctor("user-123", request)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("License number already exists");

        verify(doctorRepository)
                .existsByLicenseNumber("LIC-12345");

        verify(doctorRepository, never())
                .save(any(Doctor.class));
    }


    @Test
    void getDoctorByUserId_shouldReturnDoctor() {

        when(doctorRepository.findByUserId("user-123"))
                .thenReturn(Optional.of(doctor));

        DoctorResponse response =
                doctorService.getDoctorByUserId("user-123");

        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo("doctor-123");
        assertThat(response.getUserId())
                .isEqualTo("user-123");
        assertThat(response.getFirstName())
                .isEqualTo("John");
        assertThat(response.getLastName())
                .isEqualTo("Doe");
        assertThat(response.getSpecialization())
                .isEqualTo("Cardiology");
    }


    @Test
    void getDoctorByUserId_shouldThrowExceptionWhenNotFound() {

        when(doctorRepository.findByUserId("user-123"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                doctorService.getDoctorByUserId("user-123")
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor profile not found");
    }


    @Test
    void updateDoctor_shouldUpdateSuccessfully() {

        when(doctorRepository.findByUserId("user-123"))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.save(any(Doctor.class)))
                .thenReturn(doctor);

        DoctorResponse response =
                doctorService.updateDoctor("user-123", request);

        assertThat(response).isNotNull();
        assertThat(response.getUserId())
                .isEqualTo("user-123");
        assertThat(response.getFirstName())
                .isEqualTo("John");
        assertThat(response.getSpecialization())
                .isEqualTo("Cardiology");

        verify(doctorRepository)
                .findByUserId("user-123");

        verify(doctorRepository)
                .save(any(Doctor.class));

        verify(doctorRepository, never())
                .existsByLicenseNumber(anyString());
    }


    @Test
    void updateDoctor_shouldThrowExceptionWhenDoctorNotFound() {

        when(doctorRepository.findByUserId("user-123"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                doctorService.updateDoctor("user-123", request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Doctor profile not found");

        verify(doctorRepository, never())
                .save(any(Doctor.class));
    }


    @Test
    void updateDoctor_shouldThrowExceptionWhenLicenseAlreadyExists() {

        request.setLicenseNumber("LIC-99999");

        when(doctorRepository.findByUserId("user-123"))
                .thenReturn(Optional.of(doctor));

        when(doctorRepository.existsByLicenseNumber("LIC-99999"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                doctorService.updateDoctor("user-123", request)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("License number already exists");

        verify(doctorRepository)
                .existsByLicenseNumber("LIC-99999");

        verify(doctorRepository, never())
                .save(any(Doctor.class));
    }
}