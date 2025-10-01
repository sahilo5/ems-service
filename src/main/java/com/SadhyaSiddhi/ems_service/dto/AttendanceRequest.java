package com.SadhyaSiddhi.ems_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;
}
