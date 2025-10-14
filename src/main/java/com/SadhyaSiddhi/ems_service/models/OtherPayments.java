package com.SadhyaSiddhi.ems_service.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "other_payments")
public class OtherPayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OtherPaymentType type;

    private String remark;
    private Double amount;
    private LocalDateTime createdAt;
    private String status = "PAID";

    @ManyToOne
    @JoinColumn(name = "config_id")
    private EmployeeSalaryConfig config;

    private LocalDate date; // actual payment date

    // Getters and setters
}
