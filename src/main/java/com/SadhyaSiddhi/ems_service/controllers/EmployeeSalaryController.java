package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.SalaryConfigDto;
import com.SadhyaSiddhi.ems_service.dto.SalaryLogDto;
import com.SadhyaSiddhi.ems_service.dto.SalarySummaryDto;
import com.SadhyaSiddhi.ems_service.dto.YearlySalaryRequest;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/salary")
@RequiredArgsConstructor
public class EmployeeSalaryController {

    private final EmployeeSalaryService salaryService;

    // 1. Get all configs
    @GetMapping("/configs")
    public ResponseEntity<ApiResponse<List<SalaryConfigDto>>> getAllConfigs() {
        List<SalaryConfigDto> configs = salaryService.getAllConfigs();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Configs fetched successfully", configs,
                HttpStatus.OK.value(), java.time.LocalDateTime.now()
        ));
    }

    // 2. Update config
    @PostMapping("/configs/update")
    public ResponseEntity<ApiResponse<EmployeeSalaryConfig>> updateConfig(
            @RequestBody Map<String,String> requestBody
    ) {
        try{
            Long id = Long.parseLong(requestBody.get("id"));
            String tier = requestBody.get("category");
            Double baseAmount = requestBody.get("baseSalary") != null ? Double.parseDouble(requestBody.get("baseSalary")) : null;
            String result = salaryService.updateConfig(id, tier, baseAmount, true);

            return ResponseEntity.ok(new ApiResponse<>(
                    true, result, null,
                    HttpStatus.OK.value(), java.time.LocalDateTime.now()
            ));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Invalid input data", null,
                    HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now()
            ));
        }
    }

    // 1. Get all logs
    @GetMapping("/salary-logs/all")
    public ResponseEntity<ApiResponse<List<SalaryLogDto>>> getAllLogs() {
        List<SalaryLogDto> logs = salaryService.getAllLogs();
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Logs fetched successfully", logs,
                HttpStatus.OK.value(), java.time.LocalDateTime.now()
        ));
    }

    // 2. Get logs by user and month
    @PostMapping("/by-user-month")
    public ResponseEntity<ApiResponse<List<SalaryLogDto>>> getLogsByUserAndMonth(
            @RequestBody Map<String, String> requestBody
    ) {
        String username = requestBody.get("username");
        String salaryMonth = requestBody.get("salaryMonth"); // format: "2025-09"

        List<SalaryLogDto> logs = salaryService.getLogsByUsernameAndMonth(username, salaryMonth);

        return ResponseEntity.ok(new ApiResponse<>(
                true, "Logs fetched successfully", logs,
                HttpStatus.OK.value(), java.time.LocalDateTime.now()
        ));
    }

    // 5. Add new log
    @PostMapping("/logs/add")
    public ResponseEntity<ApiResponse<EmployeeSalaryLog>> addSalaryLog(
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String username = requestBody.get("username");
            String payPeriod = requestBody.get("payPeriod");
            Double amountPaid = Double.parseDouble(requestBody.get("amountPaid"));
            String statusStr = requestBody.get("status");
            String remarks = requestBody.get("remarks");

            String msg = salaryService.addSalaryLog(username, payPeriod, amountPaid, statusStr, remarks);

            return ResponseEntity.ok(new ApiResponse<>(
                    true, msg, null,
                    HttpStatus.OK.value(), java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false, "Failed to add salary log: " + e.getMessage(), null,
                    HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/monthly-summary")
    public ResponseEntity<ApiResponse<SalarySummaryDto>> getSalarySummary(
            @RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String month = requestBody.get("month");
        SalarySummaryDto data = salaryService.getSalarySummary(username, month);
        return ResponseEntity.ok(new ApiResponse<>(
                true, "Salary summary fetched successfully", data,
                HttpStatus.OK.value(), java.time.LocalDateTime.now()
        ));
    }

    @PostMapping("/yearly-summary")
    public ResponseEntity<Map<String, Double>> getYearlySummary(@RequestBody YearlySalaryRequest request) {
        Map<String, Double> result = salaryService.getYearlyNetSalary(request.getUsername(), request.getYear());
        return ResponseEntity.ok(result);
    }

}
