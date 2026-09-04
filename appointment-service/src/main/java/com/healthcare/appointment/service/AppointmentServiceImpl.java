package com.healthcare.appointment.service;

import com.healthcare.appointment.client.DoctorClient;
import com.healthcare.appointment.client.PatientClient;
import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.exception.ResourceNotFoundException;
import com.healthcare.appointment.model.Appointment;
import com.healthcare.appointment.model.AppointmentStatus;
import com.healthcare.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Override
    public AppointmentResponse bookAppointment(
            String patientId,
            AppointmentRequest request) {

        // Verify patient profile exists
        try {
            patientClient.getPatientById(patientId);
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "Patient profile not found"
            );
        }

        // Verify doctor profile exists
        try {
            doctorClient.getDoctorById(request.doctorId());
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                    "Doctor profile not found"
            );
        }

        // Validate appointment date and time
        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        request.appointmentDate(),
                        request.appointmentTime()
                );

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Appointment date and time must be in the future"
            );
        }

        // Check whether doctor is already booked
        boolean alreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                request.doctorId(),
                                request.appointmentDate(),
                                request.appointmentTime(),
                                AppointmentStatus.CANCELLED
                        );

        if (alreadyBooked) {
            throw new IllegalStateException(
                    "Doctor is already booked for this time"
            );
        }

        // Check whether patient already has an appointment
        boolean patientAlreadyBooked =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                                patientId,
                                request.appointmentDate(),
                                request.appointmentTime(),
                                AppointmentStatus.CANCELLED
                        );

        if (patientAlreadyBooked) {
            throw new IllegalStateException(
                    "Patient already has an appointment at this time"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = Appointment.builder()
                .patientId(patientId)
                .doctorId(request.doctorId())
                .appointmentDate(request.appointmentDate())
                .appointmentTime(request.appointmentTime())
                .reason(request.reason())
                .status(AppointmentStatus.BOOKED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse getAppointmentById(
            String appointmentId,
            String userId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        ));

        if (!appointment.getPatientId().equals(userId)
                && !appointment.getDoctorId().equals(userId)) {

            throw new IllegalStateException(
                    "You are not authorized to view this appointment"
            );
        }

        return mapToResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(
            String patientId) {

        return appointmentRepository
                .findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(
            String doctorId) {

        return appointmentRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AppointmentResponse cancelAppointment(
            String appointmentId,
            String userId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        ));

        if (!appointment.getPatientId().equals(userId)
                && !appointment.getDoctorId().equals(userId)) {

            throw new IllegalStateException(
                    "You are not authorized to cancel this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment is already cancelled"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed appointment cannot be cancelled"
            );
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse confirmAppointment(
            String appointmentId,
            String doctorId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        ));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new IllegalStateException(
                    "You are not authorized to confirm this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled appointment cannot be confirmed"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed appointment cannot be confirmed"
            );
        }

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new IllegalStateException(
                    "Only booked appointments can be confirmed"
            );
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse completeAppointment(
            String appointmentId,
            String doctorId) {

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        ));

        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new IllegalStateException(
                    "You are not authorized to complete this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cancelled appointment cannot be completed"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Appointment is already completed"
            );
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed appointments can be completed"
            );
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        return mapToResponse(updated);
    }

    private AppointmentResponse mapToResponse(
            Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}