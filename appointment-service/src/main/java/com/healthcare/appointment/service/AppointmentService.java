package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;

import java.util.List;

public interface AppointmentService {

    AppointmentResponse bookAppointment(
            String patientId,
            AppointmentRequest request
    );

    AppointmentResponse getAppointmentById(
            String appointmentId,
            String userId
    );

    List<AppointmentResponse> getPatientAppointments(
            String patientId
    );

    List<AppointmentResponse> getDoctorAppointments(
            String doctorId
    );

    AppointmentResponse cancelAppointment(
            String appointmentId,
            String userId
    );
}