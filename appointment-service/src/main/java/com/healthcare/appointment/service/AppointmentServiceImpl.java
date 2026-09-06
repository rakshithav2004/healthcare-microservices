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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;

    @Override
    public AppointmentResponse bookAppointment(
            String patientId,
            AppointmentRequest request) {

        log.info(
                "Booking appointment: patientId={}, doctorId={}, date={}, time={}",
                patientId,
                request.doctorId(),
                request.appointmentDate(),
                request.appointmentTime()
        );

        // Verify patient profile exists
        try {
            patientClient.getPatientById(patientId);
        } catch (Exception e) {
            log.warn("Patient profile not found: patientId={}", patientId);
            throw new ResourceNotFoundException(
                    "Patient profile not found"
            );
        }

        // Verify doctor profile exists
        try {
            doctorClient.getDoctorById(request.doctorId());
        } catch (Exception e) {
            log.warn("Doctor profile not found: doctorId={}", request.doctorId());
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
            log.warn(
                    "Rejected past appointment: patientId={}, doctorId={}",
                    patientId,
                    request.doctorId()
            );

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
            log.warn(
                    "Doctor slot already booked: doctorId={}, date={}, time={}",
                    request.doctorId(),
                    request.appointmentDate(),
                    request.appointmentTime()
            );

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
            log.warn(
                    "Patient already has an appointment: patientId={}, date={}, time={}",
                    patientId,
                    request.appointmentDate(),
                    request.appointmentTime()
            );

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

        log.info(
                "Appointment booked successfully: appointmentId={}, patientId={}, doctorId={}",
                saved.getId(),
                patientId,
                request.doctorId()
        );

        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse getAppointmentById(
            String appointmentId,
            String userId) {

        log.info(
                "Fetching appointment: appointmentId={}, userId={}",
                appointmentId,
                userId
        );

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn(
                            "Appointment not found: appointmentId={}",
                            appointmentId
                    );

                    return new ResourceNotFoundException(
                            "Appointment not found"
                    );
                });

        if (!appointment.getPatientId().equals(userId)
                && !appointment.getDoctorId().equals(userId)) {

            log.warn(
                    "Unauthorized appointment access: appointmentId={}, userId={}",
                    appointmentId,
                    userId
            );

            throw new IllegalStateException(
                    "You are not authorized to view this appointment"
            );
        }

        return mapToResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getPatientAppointments(
            String patientId) {

        log.info(
                "Fetching patient appointments: patientId={}",
                patientId
        );

        return appointmentRepository
                .findByPatientId(patientId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getDoctorAppointments(
            String doctorId) {

        log.info(
                "Fetching doctor appointments: doctorId={}",
                doctorId
        );

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

        log.info(
                "Cancelling appointment: appointmentId={}, userId={}",
                appointmentId,
                userId
        );

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn(
                            "Appointment not found for cancellation: appointmentId={}",
                            appointmentId
                    );

                    return new ResourceNotFoundException(
                            "Appointment not found"
                    );
                });

        if (!appointment.getPatientId().equals(userId)
                && !appointment.getDoctorId().equals(userId)) {

            log.warn(
                    "Unauthorized appointment cancellation: appointmentId={}, userId={}",
                    appointmentId,
                    userId
            );

            throw new IllegalStateException(
                    "You are not authorized to cancel this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            log.warn(
                    "Appointment already cancelled: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Appointment is already cancelled"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            log.warn(
                    "Attempt to cancel completed appointment: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Completed appointment cannot be cancelled"
            );
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        log.info(
                "Appointment cancelled successfully: appointmentId={}",
                appointmentId
        );

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse confirmAppointment(
            String appointmentId,
            String doctorId) {

        log.info(
                "Confirming appointment: appointmentId={}, doctorId={}",
                appointmentId,
                doctorId
        );

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn(
                            "Appointment not found for confirmation: appointmentId={}",
                            appointmentId
                    );

                    return new ResourceNotFoundException(
                            "Appointment not found"
                    );
                });

        if (!appointment.getDoctorId().equals(doctorId)) {
            log.warn(
                    "Unauthorized appointment confirmation: appointmentId={}, doctorId={}",
                    appointmentId,
                    doctorId
            );

            throw new IllegalStateException(
                    "You are not authorized to confirm this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            log.warn(
                    "Attempt to confirm cancelled appointment: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Cancelled appointment cannot be confirmed"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            log.warn(
                    "Attempt to confirm completed appointment: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Completed appointment cannot be confirmed"
            );
        }

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            log.warn(
                    "Invalid appointment status for confirmation: appointmentId={}, status={}",
                    appointmentId,
                    appointment.getStatus()
            );

            throw new IllegalStateException(
                    "Only booked appointments can be confirmed"
            );
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        log.info(
                "Appointment confirmed successfully: appointmentId={}, doctorId={}",
                appointmentId,
                doctorId
        );

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse completeAppointment(
            String appointmentId,
            String doctorId) {

        log.info(
                "Completing appointment: appointmentId={}, doctorId={}",
                appointmentId,
                doctorId
        );

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> {
                    log.warn(
                            "Appointment not found for completion: appointmentId={}",
                            appointmentId
                    );

                    return new ResourceNotFoundException(
                            "Appointment not found"
                    );
                });

        if (!appointment.getDoctorId().equals(doctorId)) {
            log.warn(
                    "Unauthorized appointment completion: appointmentId={}, doctorId={}",
                    appointmentId,
                    doctorId
            );

            throw new IllegalStateException(
                    "You are not authorized to complete this appointment"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            log.warn(
                    "Attempt to complete cancelled appointment: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Cancelled appointment cannot be completed"
            );
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            log.warn(
                    "Appointment already completed: appointmentId={}",
                    appointmentId
            );

            throw new IllegalStateException(
                    "Appointment is already completed"
            );
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            log.warn(
                    "Invalid appointment status for completion: appointmentId={}, status={}",
                    appointmentId,
                    appointment.getStatus()
            );

            throw new IllegalStateException(
                    "Only confirmed appointments can be completed"
            );
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);

        log.info(
                "Appointment completed successfully: appointmentId={}, doctorId={}",
                appointmentId,
                doctorId
        );

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