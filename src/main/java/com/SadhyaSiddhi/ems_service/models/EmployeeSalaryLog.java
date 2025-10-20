package com.SadhyaSiddhi.ems_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "employee_salary_log")
public class EmployeeSalaryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to config
    @ManyToOne
    @JoinColumn(name = "config_id", nullable = false)
    private EmployeeSalaryConfig config;

    @Column(nullable = false)
    private String salaryMonth;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double grossSalary;

    private Double advanceTotal;

    @Column(nullable = false)
    private Double netSalary;

    private Double otherPaymentsTotal;

  ;
}
