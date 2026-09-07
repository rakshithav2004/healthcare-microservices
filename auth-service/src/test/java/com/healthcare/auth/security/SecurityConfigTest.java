package com.healthcare.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerEndpoint_shouldBePublic() throws Exception {

        mockMvc.perform(
                get("/api/v1/auth/register")
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void loginEndpoint_shouldBePublic() throws Exception {

        mockMvc.perform(
                get("/api/v1/auth/login")
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void protectedEndpoint_withoutToken_shouldReturn401()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/auth/me")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void healthEndpoint_shouldBePublic() throws Exception {

        mockMvc.perform(
                get("/actuator/health")
        ).andExpect(status().isOk());
    }
}