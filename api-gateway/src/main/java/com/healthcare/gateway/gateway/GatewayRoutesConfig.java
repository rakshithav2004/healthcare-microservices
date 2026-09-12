package com.healthcare.gateway.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return route("auth-service")
                .route(path("/api/v1/auth/**"), http())
                .filter(lb("AUTH-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> patientRoute() {
        return route("patient-service")
                .route(path("/api/v1/patients/**"), http())
                .filter(lb("PATIENT-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> doctorRoute() {
        return route("doctor-service")
                .route(path("/api/v1/doctors/**"), http())
                .filter(lb("DOCTOR-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> appointmentRoute() {
        return route("appointment-service")
                .route(path("/api/v1/appointments/**"), http())
                .filter(lb("APPOINTMENT-SERVICE"))
                .build();
    }
}