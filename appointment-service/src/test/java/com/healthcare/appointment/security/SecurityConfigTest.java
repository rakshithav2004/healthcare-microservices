package com.healthcare.appointment.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void actuatorHealth_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/actuator/health")
                )
                .andExpect(status().isOk());
    }

    @Test
    void actuatorInfo_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/actuator/info")
                )
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUi_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/swagger-ui/index.html")
                )
                .andExpect(status().isOk());
    }

    @Test
    void apiDocs_shouldBePublic() throws Exception {

        mockMvc.perform(
                        get("/v3/api-docs")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withoutToken_shouldReturn401() throws Exception {

        mockMvc.perform(
                        get("/api/v1/appointments/patient/me")
                )
                .andExpect(status().isUnauthorized());
    }
}