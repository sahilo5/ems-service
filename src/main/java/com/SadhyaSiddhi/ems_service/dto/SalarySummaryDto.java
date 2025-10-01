package com.SadhyaSiddhi.ems_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalarySummaryDto {
    private String employeeName;
    private String username;
    private double perDayAmount;
    private double totalSalary;   // salary for working days
    private int presentDays;
    private int workingDays;
    private double leaveDeduction;
    private double halfDayDeduction;
    private double lateDeduction;
    private double deductionsTotal;
    private double calculatedSalary;
    private double bonus;
    private double net;
    private String month;
    private int daysInMonth;
    private double totalMonthSalary;
    private boolean isPaid;
}
