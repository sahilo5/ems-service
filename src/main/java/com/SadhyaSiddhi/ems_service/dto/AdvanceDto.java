package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdvanceDto {
    private Long id;
    private String employeeName;
    private String username;
    private LocalDate advanceDate;
    private String title;
    private String remark;
    private Double amount;
    private String status;
}

