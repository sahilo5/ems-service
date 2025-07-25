package com.SadhyaSiddhi.ems_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthCheck {

    @GetMapping("/user/health")
    public String healthCheckUser() {
        return "User Service is up and running!";
    }

    @GetMapping("/admin/health")
    public String healthCheckAdmin() {
        return "Admin Service is up and running!";
    }
}
