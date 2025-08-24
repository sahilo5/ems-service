package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceRequest {
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;
}
