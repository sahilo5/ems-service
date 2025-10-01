package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalaryLogDto {
    private Long id;
    private String employeeName;
    private String username;
    private String salaryMonth;
    private LocalDateTime payDay;
    private Double amountPaid;
    private String status;
    private String remarks;
}
