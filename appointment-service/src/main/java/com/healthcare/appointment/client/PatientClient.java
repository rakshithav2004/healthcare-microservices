package com.healthcare.appointment.client;

import com.healthcare.appointment.config.FeignConfig;
import com.healthcare.appointment.dto.PatientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "patient-service",
        url = "${patient-service.url}",
        configuration = FeignConfig.class
)
public interface PatientClient {

    @GetMapping("/api/v1/patients/{userId}")
    PatientResponse getPatientById(
            @PathVariable("userId") String userId
    );
}