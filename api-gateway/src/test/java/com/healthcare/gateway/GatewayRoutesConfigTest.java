package com.healthcare.gateway;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

class GatewayRoutesConfigTest {

  private final GatewayRoutesConfig config = new GatewayRoutesConfig();

  @Test
  void shouldCreateAuthRoute() {

    RouterFunction<ServerResponse> route = config.authRoute();

    assertNotNull(route);
  }

  @Test
  void shouldCreatePatientRoute() {

    RouterFunction<ServerResponse> route = config.patientRoute();

    assertNotNull(route);
  }

  @Test
  void shouldCreateDoctorRoute() {

    RouterFunction<ServerResponse> route = config.doctorRoute();

    assertNotNull(route);
  }

  @Test
  void shouldCreateAppointmentRoute() {

    RouterFunction<ServerResponse> route = config.appointmentRoute();

    assertNotNull(route);
  }
}
