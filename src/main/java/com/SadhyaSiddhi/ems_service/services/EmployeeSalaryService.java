package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.*;
import com.SadhyaSiddhi.ems_service.models.AdvanceLog;
import com.SadhyaSiddhi.ems_service.models.Advances;
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
    public String addSalaryLog(SalaryLogDto requestBody);

    //Summary
    SalarySummaryDto getSalarySummary(String username, String month);
    Map<String, Double> getYearlyNetSalary(String username, int year);

    // Advances
    AdvanceDto createAdvance(AdvanceDto advance);
    AdvanceDto updateAdvance(Long id, AdvanceDto advance);
    void deleteAdvance(Long id);
    List<AdvanceDto> getAllAdvances();
    AdvanceDto getAdvanceById(Long id);

    // Other Payments
    public OtherPaymentDto createOtherPayment(OtherPaymentDto dto);
    public void deleteOtherPayment(Long id);
    public List<OtherPaymentDto> getAllOtherPayments();
    public OtherPaymentDto updateOtherPayment(Long id, OtherPaymentDto dto);

    RepayAdvanceSummaryDto getRepayAmountsByUserName(String username);
    public OtherPaymentsSummaryDto getOtherPaymentsSummary(String username, String month);
}
