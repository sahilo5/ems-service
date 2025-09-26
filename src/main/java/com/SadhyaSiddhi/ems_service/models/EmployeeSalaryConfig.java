package com.SadhyaSiddhi.ems_service.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "employee_salary_config")
public class EmployeeSalaryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to employee (UserEntity with role = EMPLOYEE)
    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false, unique = true)
    private UserEntity employee;

    // Salary tier/category (experience/role-based)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryTier salaryTier;

    // Base/fixed monthly salary
    @Column(nullable = false)
    private Double baseAmount;

    // Whether employee is active on payroll
    @Column(nullable = false)
    private boolean active = true;

    // Track last paid period to avoid duplicate logs
    private String lastPaidPeriod;
}
