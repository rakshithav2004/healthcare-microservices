package com.healthcare.appointment.repository;

import com.healthcare.appointment.model.Appointment;
import com.healthcare.appointment.model.AppointmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByDoctorId(String doctorId);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            String doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            String patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );
}