package com.healthcare.appointment.controller;

import com.healthcare.appointment.dto.AppointmentRequest;
import com.healthcare.appointment.dto.AppointmentResponse;
import com.healthcare.appointment.model.AppointmentStatus;
import com.healthcare.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private Authentication authentication;

    private AppointmentController appointmentController;

    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {

        appointmentController =
                new AppointmentController(appointmentService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(appointmentController)
                .build();

        appointmentResponse = new AppointmentResponse(
                "appointment-1",
                "patient-1",
                "doctor-1",
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup",
                AppointmentStatus.BOOKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    @Test
    void bookAppointment_shouldReturn201() throws Exception {

        AppointmentRequest request = new AppointmentRequest(
                "doctor-1",
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                "Regular health checkup"
        );

        when(authentication.getName())
                .thenReturn("patient-1");

        when(appointmentService.bookAppointment(
                eq("patient-1"),
                any(AppointmentRequest.class)
        )).thenReturn(appointmentResponse);

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("appointment-1"))
                .andExpect(jsonPath("$.patientId").value("patient-1"))
                .andExpect(jsonPath("$.doctorId").value("doctor-1"))
                .andExpect(jsonPath("$.status").value("BOOKED"));

        verify(appointmentService)
                .bookAppointment(eq("patient-1"), any(AppointmentRequest.class));
    }


    @Test
    void getAppointment_shouldReturn200() throws Exception {

        when(authentication.getName())
                .thenReturn("patient-1");

        when(appointmentService.getAppointmentById(
                "appointment-1",
                "patient-1"
        )).thenReturn(appointmentResponse);

        mockMvc.perform(
                        get("/api/v1/appointments/appointment-1")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("appointment-1"))
                .andExpect(jsonPath("$.patientId").value("patient-1"));

        verify(appointmentService)
                .getAppointmentById("appointment-1", "patient-1");
    }


    @Test
    void getMyAppointments_shouldReturn200() throws Exception {

        when(authentication.getName())
                .thenReturn("patient-1");

        when(appointmentService.getPatientAppointments("patient-1"))
                .thenReturn(List.of(appointmentResponse));

        mockMvc.perform(
                        get("/api/v1/appointments/patient/me")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("appointment-1"))
                .andExpect(jsonPath("$[0].patientId").value("patient-1"));

        verify(appointmentService)
                .getPatientAppointments("patient-1");
    }


    @Test
    void getDoctorAppointments_shouldReturn200_whenDoctorOwnsAppointments()
            throws Exception {

        when(authentication.getName())
                .thenReturn("doctor-1");

        when(appointmentService.getDoctorAppointments("doctor-1"))
                .thenReturn(List.of(appointmentResponse));

        mockMvc.perform(
                        get("/api/v1/appointments/doctor/doctor-1")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("appointment-1"))
                .andExpect(jsonPath("$[0].doctorId").value("doctor-1"));

        verify(appointmentService)
                .getDoctorAppointments("doctor-1");
    }


    @Test
    void getDoctorAppointments_shouldThrowException_whenDoctorDoesNotOwnAppointments() {

        when(authentication.getName())
                .thenReturn("doctor-2");

        IllegalStateException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> appointmentController.getDoctorAppointments(
                                authentication,
                                "doctor-1"
                        )
                );

        assertEquals(
                "You are not authorized to view these appointments",
                exception.getMessage()
        );

        verify(appointmentService, never())
                .getDoctorAppointments(anyString());
    }


    @Test
    void cancelAppointment_shouldReturn200() throws Exception {

        AppointmentResponse cancelledResponse =
                new AppointmentResponse(
                        "appointment-1",
                        "patient-1",
                        "doctor-1",
                        appointmentResponse.appointmentDate(),
                        appointmentResponse.appointmentTime(),
                        appointmentResponse.reason(),
                        AppointmentStatus.CANCELLED,
                        appointmentResponse.createdAt(),
                        LocalDateTime.now()
                );

        when(authentication.getName())
                .thenReturn("patient-1");

        when(appointmentService.cancelAppointment(
                "appointment-1",
                "patient-1"
        )).thenReturn(cancelledResponse);

        mockMvc.perform(
                        put("/api/v1/appointments/appointment-1/cancel")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(appointmentService)
                .cancelAppointment("appointment-1", "patient-1");
    }


    @Test
    void confirmAppointment_shouldReturn200() throws Exception {

        AppointmentResponse confirmedResponse =
                new AppointmentResponse(
                        "appointment-1",
                        "patient-1",
                        "doctor-1",
                        appointmentResponse.appointmentDate(),
                        appointmentResponse.appointmentTime(),
                        appointmentResponse.reason(),
                        AppointmentStatus.CONFIRMED,
                        appointmentResponse.createdAt(),
                        LocalDateTime.now()
                );

        when(authentication.getName())
                .thenReturn("doctor-1");

        when(appointmentService.confirmAppointment(
                "appointment-1",
                "doctor-1"
        )).thenReturn(confirmedResponse);

        mockMvc.perform(
                        put("/api/v1/appointments/appointment-1/confirm")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(appointmentService)
                .confirmAppointment("appointment-1", "doctor-1");
    }


    @Test
    void completeAppointment_shouldReturn200() throws Exception {

        AppointmentResponse completedResponse =
                new AppointmentResponse(
                        "appointment-1",
                        "patient-1",
                        "doctor-1",
                        appointmentResponse.appointmentDate(),
                        appointmentResponse.appointmentTime(),
                        appointmentResponse.reason(),
                        AppointmentStatus.COMPLETED,
                        appointmentResponse.createdAt(),
                        LocalDateTime.now()
                );

        when(authentication.getName())
                .thenReturn("doctor-1");

        when(appointmentService.completeAppointment(
                "appointment-1",
                "doctor-1"
        )).thenReturn(completedResponse);

        mockMvc.perform(
                        put("/api/v1/appointments/appointment-1/complete")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(appointmentService)
                .completeAppointment("appointment-1", "doctor-1");
    }
}