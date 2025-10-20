package com.SadhyaSiddhi.ems_service.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryLogDto {
    private Long id;
    private String employeeName;
    private String username;
    private String salaryMonth;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double grossSalary;
    private Double advanceTotal;
    private Double netSalary;
    private Double otherPaymentsTotal;
}
