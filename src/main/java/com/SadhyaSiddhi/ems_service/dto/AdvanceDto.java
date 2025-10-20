package com.SadhyaSiddhi.ems_service.dto;

import com.SadhyaSiddhi.ems_service.models.AdvanceStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AdvanceDto {
    private Long id;
    private String employeeName;
    private String username;
    private LocalDate advanceDate;
    private LocalDate repayDate;
    private String title;
    private String remark;
    private Double amount;
    private AdvanceStatus status;
}
