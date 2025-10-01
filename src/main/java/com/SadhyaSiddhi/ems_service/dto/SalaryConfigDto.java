package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;

@Data
public class SalaryConfigDto {
    private Long id;
    private String employeeName;
    private String username;
    private String category;
    private Double baseSalary;
}
