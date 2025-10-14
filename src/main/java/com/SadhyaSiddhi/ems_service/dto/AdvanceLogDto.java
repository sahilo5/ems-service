package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdvanceLogDto {
    private Long id;
    private Long advanceId;
    private Double paidAmount;
    private LocalDateTime paidDate;
    private String remarks;
    private String status;
}

