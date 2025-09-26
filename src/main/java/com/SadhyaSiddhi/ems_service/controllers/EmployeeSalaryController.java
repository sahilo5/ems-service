package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import com.SadhyaSiddhi.ems_service.services.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class EmployeeSalaryController {

    private final EmployeeSalaryService salaryService;

    // -------- Config Endpoints ----------
    @PostMapping("/config")
    public ResponseEntity<EmployeeSalaryConfig> createConfig(@RequestBody EmployeeSalaryConfig config) {
        return ResponseEntity.ok(salaryService.createConfig(config));
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<EmployeeSalaryConfig> updateConfig(@PathVariable Long id, @RequestBody EmployeeSalaryConfig config) {
        return ResponseEntity.ok(salaryService.updateConfig(id, config));
    }

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        salaryService.deleteConfig(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config/{id}")
    public ResponseEntity<EmployeeSalaryConfig> getConfigById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.getConfigById(id));
    }

    @GetMapping("/config")
    public ResponseEntity<List<EmployeeSalaryConfig>> getAllConfigs() {
        return ResponseEntity.ok(salaryService.getAllConfigs());
    }

    // -------- Log Endpoints ----------
    @PostMapping("/log/{configId}")
    public ResponseEntity<EmployeeSalaryLog> createLog(@RequestBody EmployeeSalaryLog log, @PathVariable Long configId) {
        return ResponseEntity.ok(salaryService.createLog(log, configId));
    }

    @PutMapping("/log/{id}")
    public ResponseEntity<EmployeeSalaryLog> updateLog(@PathVariable Long id, @RequestBody EmployeeSalaryLog log) {
        return ResponseEntity.ok(salaryService.updateLog(id, log));
    }

    @DeleteMapping("/log/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        salaryService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/log/{id}")
    public ResponseEntity<EmployeeSalaryLog> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(salaryService.getLogById(id));
    }

    @GetMapping("/log/config/{configId}")
    public ResponseEntity<List<EmployeeSalaryLog>> getLogsByConfig(@PathVariable Long configId) {
        return ResponseEntity.ok(salaryService.getLogsByConfig(configId));
    }

    @GetMapping("/log")
    public ResponseEntity<List<EmployeeSalaryLog>> getAllLogs() {
        return ResponseEntity.ok(salaryService.getAllLogs());
    }
}
