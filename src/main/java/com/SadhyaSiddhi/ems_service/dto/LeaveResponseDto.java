package com.SadhyaSiddhi.ems_service.dto;

import com.SadhyaSiddhi.ems_service.models.LeaveStatus;
import com.SadhyaSiddhi.ems_service.models.LeaveType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveResponseDto {
    private Long id;
    private LeaveType type;
    private String reason;
    private List<LocalDate> dates;
    private int noOfDays;
    private String description;
    private LeaveStatus status;
    private String rejectionMessage;
    private LocalDate createdAt;
    private String EmployeeName;
}
