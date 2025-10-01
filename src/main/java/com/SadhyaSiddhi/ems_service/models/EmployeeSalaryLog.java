package com.SadhyaSiddhi.ems_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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

    // Pay period (like 2025-09 for September 2025)
    @Column(nullable = false)
    private String salaryMonth;

    // Final payout (can differ from baseAmount if bonus/deductions applied)
    @Column(nullable = false)
    private Double amountPaid;

    // Status of payment
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryStatus status = SalaryStatus.PENDING;

    // When actually paid
    private LocalDateTime payDay;

    // Any additional remarks (bonus reason, deductions, etc.)
    private String remarks;
}
