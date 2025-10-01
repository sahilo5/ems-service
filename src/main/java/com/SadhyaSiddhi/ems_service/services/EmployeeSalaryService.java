package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.SalaryConfigDto;
import com.SadhyaSiddhi.ems_service.dto.SalaryLogDto;
import com.SadhyaSiddhi.ems_service.dto.SalarySummaryDto;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeSalaryService {

    // Config
    List<SalaryConfigDto> getAllConfigs();
    String updateConfig(Long id, String tier, Double baseAmount, Boolean active);

    // Logs
    List<SalaryLogDto> getAllLogs();
    List<SalaryLogDto> getLogsByUsernameAndMonth(String username, String salaryMonth);
    public String addSalaryLog(String username, String payPeriod, Double amountPaid, String statusStr, String remarks);

    //Summary
    public SalarySummaryDto getSalarySummary(String username, String month);
    Map<String, Double> getYearlyNetSalary(String username, int year);
}
