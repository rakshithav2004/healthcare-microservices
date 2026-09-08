package com.healthcare.patient.controller;

import com.healthcare.patient.dto.PatientRequest;
import com.healthcare.patient.dto.PatientResponse;
import com.healthcare.patient.security.JwtService;
import com.healthcare.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtService jwtService;

    private PatientRequest createValidRequest() {

        return new PatientRequest(
                "John",
                "Doe",
                LocalDate.of(1995, 5, 10),
                "MALE",
                "9876543210",
                "Bangalore"
        );
    }

    private PatientResponse createResponse() {

        return PatientResponse.builder()
                .id("patient-123")
                .userId("user-123")
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, 5, 10))
                .gender("MALE")
                .phone("9876543210")
                .address("Bangalore")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Authentication createAuthentication() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("user-123");

        return authentication;
    }

    @Test
    void createPatient_shouldReturn201() throws Exception {

        PatientRequest request = createValidRequest();
        PatientResponse response = createResponse();

        when(patientService.createPatient(
                eq("user-123"),
                any(PatientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/patients")
                                .principal(createAuthentication())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("patient-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.phone").value("9876543210"));
    }

    @Test
    void getMyProfile_shouldReturn200() throws Exception {

        PatientResponse response = createResponse();

        when(patientService.getPatientByUserId("user-123"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/patients/me")
                                .principal(createAuthentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("patient-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void updateMyProfile_shouldReturn200() throws Exception {

        PatientRequest request = createValidRequest();
        PatientResponse response = createResponse();

        when(patientService.updatePatient(
                eq("user-123"),
                any(PatientRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/patients/me")
                                .principal(createAuthentication())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("patient-123"))
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getPatientByUserId_shouldReturn200() throws Exception {

        PatientResponse response = createResponse();

        when(patientService.getPatientByUserId("user-123"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/patients/user-123")
                                .principal(createAuthentication())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("patient-123"))
                .andExpect(jsonPath("$.userId").value("user-123"));
    }

    @Test
    void getPatientByUserId_forDifferentUser_shouldReturn500()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/patients/other-user")
                                .principal(createAuthentication())
                )
                .andExpect(status().is5xxServerError());
    }

    @Test
    void createPatient_withInvalidRequest_shouldReturn400()
            throws Exception {

        PatientRequest request = new PatientRequest(
                "",
                "",
                LocalDate.now().plusDays(1),
                "INVALID",
                "123",
                ""
        );

        mockMvc.perform(
                        post("/api/v1/patients")
                                .principal(createAuthentication())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}