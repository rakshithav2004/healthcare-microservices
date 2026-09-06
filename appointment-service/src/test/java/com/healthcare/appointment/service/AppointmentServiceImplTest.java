package com.healthcare.appointment.service;

import com.healthcare.appointment.client.DoctorClient;
import com.healthcare.appointment.client.PatientClient;
import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.model.Appointment;
import com.healthcare.appointment.model.AppointmentStatus;
import com.healthcare.appointment.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientClient patientClient;

    @Mock
    private DoctorClient doctorClient;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;


    @Test
    void bookAppointment_shouldCreateAppointmentSuccessfully() {

        String patientId = "patient-1";
        String doctorId = "doctor-1";

        AppointmentRequest request = new AppointmentRequest(
                doctorId,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        Appointment savedAppointment = Appointment.builder()
                .id("appointment-1")
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentDate(request.appointmentDate())
                .appointmentTime(request.appointmentTime())
                .reason(request.reason())
                .status(AppointmentStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctorId,
                        request.appointmentDate(),
                        request.appointmentTime(),
                        AppointmentStatus.CANCELLED
                ))
                .thenReturn(false);

        when(appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        patientId,
                        request.appointmentDate(),
                        request.appointmentTime(),
                        AppointmentStatus.CANCELLED
                ))
                .thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(savedAppointment);

        AppointmentResponse response =
                appointmentService.bookAppointment(patientId, request);

        assertNotNull(response);
        assertEquals("appointment-1", response.id());
        assertEquals(patientId, response.patientId());
        assertEquals(doctorId, response.doctorId());
        assertEquals(AppointmentStatus.BOOKED, response.status());

        verify(patientClient).getPatientById(patientId);
        verify(doctorClient).getDoctorById(doctorId);
        verify(appointmentRepository).save(any(Appointment.class));
    }


    @Test
    void bookAppointment_shouldThrowException_whenPatientDoesNotExist() {

        String patientId = "patient-1";

        AppointmentRequest request = new AppointmentRequest(
                "doctor-1",
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        when(patientClient.getPatientById(patientId))
                .thenThrow(new RuntimeException("Patient not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.bookAppointment(patientId, request)
        );

        assertEquals("Patient profile not found", exception.getMessage());

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void bookAppointment_shouldThrowException_whenDoctorDoesNotExist() {

        String patientId = "patient-1";
        String doctorId = "doctor-1";

        AppointmentRequest request = new AppointmentRequest(
                doctorId,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        when(doctorClient.getDoctorById(doctorId))
                .thenThrow(new RuntimeException("Doctor not found"));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentService.bookAppointment(patientId, request)
        );

        assertEquals("Doctor profile not found", exception.getMessage());

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void bookAppointment_shouldThrowException_whenDoctorAlreadyBooked() {

        String patientId = "patient-1";
        String doctorId = "doctor-1";

        AppointmentRequest request = new AppointmentRequest(
                doctorId,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctorId,
                        request.appointmentDate(),
                        request.appointmentTime(),
                        AppointmentStatus.CANCELLED
                ))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.bookAppointment(patientId, request)
        );

        assertEquals(
                "Doctor is already booked for this time",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void bookAppointment_shouldThrowException_whenPatientAlreadyBooked() {

        String patientId = "patient-1";
        String doctorId = "doctor-1";

        AppointmentRequest request = new AppointmentRequest(
                doctorId,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctorId,
                        request.appointmentDate(),
                        request.appointmentTime(),
                        AppointmentStatus.CANCELLED
                ))
                .thenReturn(false);

        when(appointmentRepository
                .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        patientId,
                        request.appointmentDate(),
                        request.appointmentTime(),
                        AppointmentStatus.CANCELLED
                ))
                .thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.bookAppointment(patientId, request)
        );

        assertEquals(
                "Patient already has an appointment at this time",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void getAppointmentById_shouldReturnAppointment_whenUserIsPatient() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        AppointmentResponse response =
                appointmentService.getAppointmentById(
                        "appointment-1",
                        "patient-1"
                );

        assertNotNull(response);
        assertEquals("appointment-1", response.id());
        assertEquals("patient-1", response.patientId());
    }


    @Test
    void getAppointmentById_shouldThrowException_whenUserIsUnauthorized() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.getAppointmentById(
                        "appointment-1",
                        "another-user"
                )
        );

        assertEquals(
                "You are not authorized to view this appointment",
                exception.getMessage()
        );
    }


    @Test
    void cancelAppointment_shouldCancelAppointmentSuccessfully() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response =
                appointmentService.cancelAppointment(
                        "appointment-1",
                        "patient-1"
                );

        assertEquals(AppointmentStatus.CANCELLED, response.status());

        verify(appointmentRepository).save(appointment);
    }


    @Test
    void cancelAppointment_shouldThrowException_whenAppointmentAlreadyCancelled() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.CANCELLED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.cancelAppointment(
                        "appointment-1",
                        "patient-1"
                )
        );

        assertEquals(
                "Appointment is already cancelled",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void cancelAppointment_shouldThrowException_whenAppointmentIsCompleted() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.COMPLETED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.cancelAppointment(
                        "appointment-1",
                        "patient-1"
                )
        );

        assertEquals(
                "Completed appointment cannot be cancelled",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void confirmAppointment_shouldConfirmBookedAppointment() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response =
                appointmentService.confirmAppointment(
                        "appointment-1",
                        "doctor-1"
                );

        assertEquals(AppointmentStatus.CONFIRMED, response.status());

        verify(appointmentRepository).save(appointment);
    }


    @Test
    void confirmAppointment_shouldThrowException_whenDoctorIsUnauthorized() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.confirmAppointment(
                        "appointment-1",
                        "another-doctor"
                )
        );

        assertEquals(
                "You are not authorized to confirm this appointment",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void confirmAppointment_shouldThrowException_whenAppointmentIsCancelled() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.CANCELLED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.confirmAppointment(
                        "appointment-1",
                        "doctor-1"
                )
        );

        assertEquals(
                "Cancelled appointment cannot be confirmed",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void completeAppointment_shouldCompleteConfirmedAppointment() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.CONFIRMED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response =
                appointmentService.completeAppointment(
                        "appointment-1",
                        "doctor-1"
                );

        assertEquals(AppointmentStatus.COMPLETED, response.status());

        verify(appointmentRepository).save(appointment);
    }


    @Test
    void completeAppointment_shouldThrowException_whenAppointmentIsNotConfirmed() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findById("appointment-1"))
                .thenReturn(Optional.of(appointment));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentService.completeAppointment(
                        "appointment-1",
                        "doctor-1"
                )
        );

        assertEquals(
                "Only confirmed appointments can be completed",
                exception.getMessage()
        );

        verify(appointmentRepository, never()).save(any());
    }


    @Test
    void getPatientAppointments_shouldReturnPatientAppointments() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findByPatientId("patient-1"))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> response =
                appointmentService.getPatientAppointments("patient-1");

        assertEquals(1, response.size());
        assertEquals("appointment-1", response.get(0).id());
        assertEquals("patient-1", response.get(0).patientId());
    }


    @Test
    void getDoctorAppointments_shouldReturnDoctorAppointments() {

        Appointment appointment = createAppointment(
                "appointment-1",
                "patient-1",
                "doctor-1",
                AppointmentStatus.BOOKED
        );

        when(appointmentRepository.findByDoctorId("doctor-1"))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> response =
                appointmentService.getDoctorAppointments("doctor-1");

        assertEquals(1, response.size());
        assertEquals("appointment-1", response.get(0).id());
        assertEquals("doctor-1", response.get(0).doctorId());
    }


    private Appointment createAppointment(
            String id,
            String patientId,
            String doctorId,
            AppointmentStatus status) {

        return Appointment.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentDate(LocalDate.now().plusDays(2))
                .appointmentTime(LocalTime.of(10, 0))
                .reason("Regular health checkup")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}