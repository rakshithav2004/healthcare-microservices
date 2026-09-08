package com.healthcare.patient.service;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.exception.ResourceAlreadyExistsException;
import com.healthcare.patient.exception.ResourceNotFoundException;
import com.healthcare.patient.model.Patient;
import com.healthcare.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    private PatientRepository patientRepository;
    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        patientRepository = mock(PatientRepository.class);
        patientService = new PatientServiceImpl(patientRepository);
    }

    @Test
    void createPatient_shouldCreateSuccessfully() {

        String userId = "user-123";

        PatientRequest request = new PatientRequest(
                "John",
                "Doe",
                LocalDate.of(1995, 5, 10),
                "MALE",
                "9876543210",
                "Bangalore"
        );

        Patient savedPatient = Patient.builder()
                .id("patient-123")
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(request.getDateOfBirth())
                .gender("MALE")
                .phone("9876543210")
                .address("Bangalore")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(patientRepository.existsByUserId(userId))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenReturn(savedPatient);

        PatientResponse response =
                patientService.createPatient(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("patient-123");
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getGender()).isEqualTo("MALE");
        assertThat(response.getPhone()).isEqualTo("9876543210");

        verify(patientRepository).existsByUserId(userId);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_whenProfileAlreadyExists_shouldThrowException() {

        String userId = "user-123";

        PatientRequest request = new PatientRequest(
                "John",
                "Doe",
                LocalDate.of(1995, 5, 10),
                "MALE",
                "9876543210",
                "Bangalore"
        );

        when(patientRepository.existsByUserId(userId))
                .thenReturn(true);

        assertThatThrownBy(() ->
                patientService.createPatient(userId, request)
        )
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Patient profile already exists");

        verify(patientRepository).existsByUserId(userId);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatient_shouldMapRequestToPatient() {

        String userId = "user-123";

        PatientRequest request = new PatientRequest(
                "Jane",
                "Smith",
                LocalDate.of(1998, 8, 20),
                "FEMALE",
                "9123456789",
                "Mysore"
        );

        when(patientRepository.existsByUserId(userId))
                .thenReturn(false);

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> {
                    Patient patient = invocation.getArgument(0);
                    patient.setId("patient-456");
                    return patient;
                });

        patientService.createPatient(userId, request);

        ArgumentCaptor<Patient> captor =
                ArgumentCaptor.forClass(Patient.class);

        verify(patientRepository).save(captor.capture());

        Patient savedPatient = captor.getValue();

        assertThat(savedPatient.getUserId()).isEqualTo(userId);
        assertThat(savedPatient.getFirstName()).isEqualTo("Jane");
        assertThat(savedPatient.getLastName()).isEqualTo("Smith");
        assertThat(savedPatient.getDateOfBirth())
                .isEqualTo(LocalDate.of(1998, 8, 20));
        assertThat(savedPatient.getGender()).isEqualTo("FEMALE");
        assertThat(savedPatient.getPhone()).isEqualTo("9123456789");
        assertThat(savedPatient.getAddress()).isEqualTo("Mysore");
        assertThat(savedPatient.getCreatedAt()).isNotNull();
        assertThat(savedPatient.getUpdatedAt()).isNotNull();
    }

    @Test
    void getPatientByUserId_shouldReturnPatient() {

        String userId = "user-123";

        Patient patient = Patient.builder()
                .id("patient-123")
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .phone("9876543210")
                .address("Bangalore")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(patientRepository.findByUserId(userId))
                .thenReturn(Optional.of(patient));

        PatientResponse response =
                patientService.getPatientByUserId(userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("patient-123");
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getDateOfBirth())
                .isEqualTo(LocalDate.of(1995, 5, 10));
        assertThat(response.getGender()).isEqualTo("MALE");
        assertThat(response.getPhone()).isEqualTo("9876543210");
        assertThat(response.getAddress()).isEqualTo("Bangalore");

        verify(patientRepository).findByUserId(userId);
    }

    @Test
    void getPatientByUserId_whenPatientNotFound_shouldThrowException() {

        String userId = "unknown-user";

        when(patientRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                patientService.getPatientByUserId(userId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient profile not found");

        verify(patientRepository).findByUserId(userId);
    }

    @Test
    void updatePatient_shouldUpdateSuccessfully() {

        String userId = "user-123";

        Patient existingPatient = Patient.builder()
                .id("patient-123")
                .userId(userId)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .phone("9876543210")
                .address("Bangalore")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        PatientRequest request = new PatientRequest(
                "Johnny",
                "Doe",
                LocalDate.of(1995, 5, 10),
                "MALE",
                "9123456789",
                "Mysore"
        );

        when(patientRepository.findByUserId(userId))
                .thenReturn(Optional.of(existingPatient));

        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response =
                patientService.updatePatient(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("patient-123");
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getFirstName()).isEqualTo("Johnny");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getPhone()).isEqualTo("9123456789");
        assertThat(response.getAddress()).isEqualTo("Mysore");

        assertThat(response.getUpdatedAt()).isNotNull();

        verify(patientRepository).findByUserId(userId);
        verify(patientRepository).save(existingPatient);
    }

    @Test
    void updatePatient_whenPatientNotFound_shouldThrowException() {

        String userId = "unknown-user";

        PatientRequest request = new PatientRequest(
                "John",
                "Doe",
                LocalDate.of(1995, 5, 10),
                "MALE",
                "9876543210",
                "Bangalore"
        );

        when(patientRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                patientService.updatePatient(userId, request)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient profile not found");

        verify(patientRepository).findByUserId(userId);
        verify(patientRepository, never()).save(any(Patient.class));
    }
}