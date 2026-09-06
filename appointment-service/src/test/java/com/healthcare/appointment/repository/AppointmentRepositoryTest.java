package com.healthcare.appointment.repository;

import com.healthcare.appointment.model.Appointment;
import com.healthcare.appointment.model.AppointmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void findByPatientId_shouldReturnPatientAppointments() {

        Appointment appointment = createAppointment(
                "appointment-patient-test",
                "patient-test",
                "doctor-test",
                AppointmentStatus.BOOKED
        );

        appointmentRepository.save(appointment);

        List<Appointment> appointments =
                appointmentRepository.findByPatientId("patient-test");

        assertFalse(appointments.isEmpty());
        assertTrue(
                appointments.stream()
                        .anyMatch(a -> a.getId().equals("appointment-patient-test"))
        );
    }

    @Test
    void findByDoctorId_shouldReturnDoctorAppointments() {

        Appointment appointment = createAppointment(
                "appointment-doctor-test",
                "patient-test",
                "doctor-test",
                AppointmentStatus.BOOKED
        );

        appointmentRepository.save(appointment);

        List<Appointment> appointments =
                appointmentRepository.findByDoctorId("doctor-test");

        assertFalse(appointments.isEmpty());
        assertTrue(
                appointments.stream()
                        .anyMatch(a -> a.getId().equals("appointment-doctor-test"))
        );
    }

    @Test
    void existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot_shouldReturnTrue() {

        LocalDate date = LocalDate.now().plusDays(10);
        LocalTime time = LocalTime.of(10, 0);

        Appointment appointment = Appointment.builder()
                .id("doctor-conflict-test")
                .patientId("patient-test")
                .doctorId("doctor-conflict")
                .appointmentDate(date)
                .appointmentTime(time)
                .reason("Doctor conflict test")
                .status(AppointmentStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment);

        boolean result =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                "doctor-conflict",
                                date,
                                time,
                                AppointmentStatus.CANCELLED
                        );

        assertTrue(result);
    }

    @Test
    void existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot_shouldReturnFalseForCancelledAppointment() {

        LocalDate date = LocalDate.now().plusDays(11);
        LocalTime time = LocalTime.of(11, 0);

        Appointment appointment = Appointment.builder()
                .id("doctor-cancelled-test")
                .patientId("patient-test")
                .doctorId("doctor-cancelled")
                .appointmentDate(date)
                .appointmentTime(time)
                .reason("Cancelled appointment test")
                .status(AppointmentStatus.CANCELLED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment);

        boolean result =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                "doctor-cancelled",
                                date,
                                time,
                                AppointmentStatus.CANCELLED
                        );

        assertFalse(result);
    }

    @Test
    void existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot_shouldReturnTrue() {

        LocalDate date = LocalDate.now().plusDays(12);
        LocalTime time = LocalTime.of(14, 0);

        Appointment appointment = Appointment.builder()
                .id("patient-conflict-test")
                .patientId("patient-conflict")
                .doctorId("doctor-test")
                .appointmentDate(date)
                .appointmentTime(time)
                .reason("Patient conflict test")
                .status(AppointmentStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment);

        boolean result =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                "patient-conflict",
                                date,
                                time,
                                AppointmentStatus.CANCELLED
                        );

        assertTrue(result);
    }

    @Test
    void existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot_shouldReturnFalseForCancelledAppointment() {

        LocalDate date = LocalDate.now().plusDays(13);
        LocalTime time = LocalTime.of(15, 0);

        Appointment appointment = Appointment.builder()
                .id("patient-cancelled-test")
                .patientId("patient-cancelled")
                .doctorId("doctor-test")
                .appointmentDate(date)
                .appointmentTime(time)
                .reason("Cancelled patient appointment")
                .status(AppointmentStatus.CANCELLED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        appointmentRepository.save(appointment);

        boolean result =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                "patient-cancelled",
                                date,
                                time,
                                AppointmentStatus.CANCELLED
                        );

        assertFalse(result);
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
                .appointmentDate(LocalDate.now().plusDays(5))
                .appointmentTime(LocalTime.of(10, 0))
                .reason("Repository test")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}