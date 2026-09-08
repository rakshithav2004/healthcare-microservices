package com.healthcare.doctor.controller;

import com.healthcare.doctor.dto.DoctorRequest;
import com.healthcare.doctor.dto.DoctorResponse;
import com.healthcare.doctor.security.JwtService;
import com.healthcare.doctor.service.DoctorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;


    private DoctorRequest validRequest() {

        DoctorRequest request = new DoctorRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setSpecialization("Cardiology");
        request.setQualification("MBBS");
        request.setLicenseNumber("LIC-12345");
        request.setExperienceYears(5);
        request.setPhone("9876543210");
        request.setHospital("City Hospital");
        request.setAddress("Bangalore");

        return request;
    }


    private DoctorResponse validResponse() {

        return DoctorResponse.builder()
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
    @WithMockUser(username = "user-123", roles = "DOCTOR")
    void createDoctor_shouldReturn201() throws Exception {

        when(
                doctorService.createDoctor(
                        eq("user-123"),
                        any(DoctorRequest.class)
                )
        ).thenReturn(validResponse());

        mockMvc.perform(
                        post("/api/v1/doctors")
                                .with(csrf())
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(
                                                validRequest()
                                        )
                                )
                )
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "user-123", roles = "DOCTOR")
    void getMyProfile_shouldReturn200() throws Exception {

        when(
                doctorService.getDoctorByUserId("user-123")
        ).thenReturn(validResponse());

        mockMvc.perform(
                        get("/api/v1/doctors/me")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user-123", roles = "DOCTOR")
    void updateMyProfile_shouldReturn200() throws Exception {

        when(
                doctorService.updateDoctor(
                        eq("user-123"),
                        any(DoctorRequest.class)
                )
        ).thenReturn(validResponse());

        mockMvc.perform(
                        put("/api/v1/doctors/me")
                                .with(csrf())
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(
                                                validRequest()
                                        )
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user-123", roles = "DOCTOR")
    void getDoctorByUserId_shouldReturn200() throws Exception {

        when(
                doctorService.getDoctorByUserId("user-123")
        ).thenReturn(validResponse());

        mockMvc.perform(
                        get("/api/v1/doctors/user-123")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user-123", roles = "DOCTOR")
    void getDoctorByUserId_forDifferentUser_shouldReturn409()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/doctors/another-user")
                )
                .andExpect(status().isConflict());
    }
}