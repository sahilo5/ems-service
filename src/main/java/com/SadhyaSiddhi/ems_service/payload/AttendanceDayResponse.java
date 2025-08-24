package com.SadhyaSiddhi.ems_service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class AttendanceDayResponse {
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private long totalHours;
    private String status;
}
