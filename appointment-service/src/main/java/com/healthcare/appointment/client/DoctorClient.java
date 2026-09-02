package com.healthcare.appointment.client;

import com.healthcare.appointment.config.FeignConfig;
import com.healthcare.appointment.dto.DoctorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "doctor-service",
        url = "${doctor-service.url}",
        configuration = FeignConfig.class
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/{userId}")
    DoctorResponse getDoctorById(
            @PathVariable("userId") String userId
    );
}