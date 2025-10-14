package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.*;
import com.SadhyaSiddhi.ems_service.models.AdvanceLog;
import com.SadhyaSiddhi.ems_service.models.Advances;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.EmployeeSalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Advances Endpoints
    @PostMapping("/advances")
    public ResponseEntity<ApiResponse<AdvanceDto>> createAdvance(@RequestBody AdvanceDto advance) {
        AdvanceDto created = salaryService.createAdvance(advance);
        return ResponseEntity.ok(new ApiResponse<>(true, "Advance created", created, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    @PostMapping("/advances/{id}")
    public ResponseEntity<ApiResponse<AdvanceDto>> updateAdvance(@PathVariable Long id, @RequestBody AdvanceDto advance) {
        AdvanceDto updated = salaryService.updateAdvance(id, advance);
        return ResponseEntity.ok(new ApiResponse<>(true, "Advance updated", updated, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    @GetMapping("/advances/{id}")
    public ResponseEntity<ApiResponse<AdvanceDto>> getAdvanceById(@PathVariable Long id) {
        AdvanceDto advance = salaryService.getAdvanceById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Advance fetched", advance, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    @PostMapping("/advances/{id}/log")
    public ResponseEntity<ApiResponse<AdvanceLogDto>> logAdvancePayment(@PathVariable Long id, @RequestBody AdvanceLogDto log) {
        AdvanceLogDto createdLog = salaryService.logAdvancePayment(id, log);
        return ResponseEntity.ok(new ApiResponse<>(true, "Advance payment logged", createdLog, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    @GetMapping("/advances/{id}/logs")
    public ResponseEntity<ApiResponse<List<AdvanceLogDto>>> getAdvanceLogs(@PathVariable Long id) {
        List<AdvanceLogDto> logs = salaryService.getAdvanceLogs(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Advance logs fetched", logs, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    @GetMapping("/advances")
    public ResponseEntity<ApiResponse<List<AdvanceDto>>> getAllAdvances() {
        List<AdvanceDto> advances = salaryService.getAllAdvances();
        return ResponseEntity.ok(new ApiResponse<>(true, "Advances fetched", advances, HttpStatus.OK.value(), LocalDateTime.now()));
    }

    // OtherPayments APIs
    @PostMapping("/other-payments")
    public ResponseEntity<ApiResponse<OtherPaymentDto>> createOtherPayment(@RequestBody OtherPaymentDto dto) {
        OtherPaymentDto created = salaryService.createOtherPayment(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Other payment created", created, HttpStatus.OK.value(), java.time.LocalDateTime.now()));
    }

    @PostMapping("/other-payments/{id}")
    public ResponseEntity<ApiResponse<OtherPaymentDto>> updateOtherPayment(@PathVariable Long id, @RequestBody OtherPaymentDto dto) {
        OtherPaymentDto updated = salaryService.updateOtherPayment(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Other payment updated", updated, HttpStatus.OK.value(), java.time.LocalDateTime.now()));
    }

    @GetMapping("/other-payments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOtherPayment(@PathVariable Long id) {
        salaryService.deleteOtherPayment(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Other payment deleted", null, HttpStatus.OK.value(), java.time.LocalDateTime.now()));
    }

    @GetMapping("/other-payments")
    public ResponseEntity<ApiResponse<List<OtherPaymentDto>>> getAllOtherPayments() {
        List<OtherPaymentDto> payments = salaryService.getAllOtherPayments();
        return ResponseEntity.ok(new ApiResponse<>(true, "Other payments fetched", payments, HttpStatus.OK.value(), java.time.LocalDateTime.now()));
    }

}
