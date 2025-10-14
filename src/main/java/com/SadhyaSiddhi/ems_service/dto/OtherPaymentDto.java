package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;
;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OtherPaymentDto {
    private Long id;
    private String type;
    private String remark;
    private Double amount;
    private LocalDateTime createdAt;
    private String status;
    private String username;
    private String employeeName;
    private LocalDate date;

    // Getters and setters
}
