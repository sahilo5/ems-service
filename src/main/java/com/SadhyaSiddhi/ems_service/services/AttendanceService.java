package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.AttendanceRequest;
import com.SadhyaSiddhi.ems_service.payload.AttendanceDayResponse;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface AttendanceService {

    public String generateQrToken() ;

    public ApiResponse<Object> markAttendance(String qrToken, String inOrOut);

    public ApiResponse<Object> markAttendanceManual(String username, String inOrOut);

    public ApiResponse<Object> updateAttendance(String username, LocalDate date, LocalDateTime checkIn, LocalDateTime checkOut);

    public List<ApiResponse<Object>> markBulkAttendance(List<Map<String, String>> bulkRequests);

    public List<AttendanceDayResponse> getAttendanceBetweenDates(AttendanceRequest request);
}
