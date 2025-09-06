package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.AttendanceRequest;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.payload.AttendanceDayResponse;
import com.SadhyaSiddhi.ems_service.services.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Endpoint for Admin to generate rotating QR tokens
     */
    @GetMapping("/admin/attendance/generate-qr")
    public ResponseEntity<ApiResponse<Object>> generateQr() {
        String qrToken = attendanceService.generateQrToken();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "QR Token generated", qrToken, 200, java.time.LocalDateTime.now())
        );
    }

    /**
     * Endpoint for Employees to scan QR and mark attendance
     */
    @PostMapping("/user/attendance/mark")
    public ApiResponse<Object> markAttendance(@RequestBody Map<String,String> requestBody) {
        Map <String, String> reqBody = requestBody;
        String qrToken = reqBody.get("token");
        String inOrOut = reqBody.get("inOrOut");
        ApiResponse<Object> response = attendanceService.markAttendance(qrToken, inOrOut);
        return response;
    }

    @PostMapping("/admin/attendance/mark")
    public ApiResponse<Object> markAttendanceManual(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String inOrOut = requestBody.get("inOrOut");
        return attendanceService.markAttendanceManual(username, inOrOut);
    }

    @PostMapping("/admin/attendance/update")
    public ApiResponse<Object> updateAttendance(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        LocalDateTime checkIn = requestBody.get("checkin") != null ?
                LocalDateTime.parse(requestBody.get("checkin")) : null;
        LocalDateTime checkOut = requestBody.get("checkout") != null ?
                LocalDateTime.parse(requestBody.get("checkout")) : null;
        LocalDate date = requestBody.get("date") != null ?
                LocalDate.parse(requestBody.get("date")) : null;

        return attendanceService.updateAttendance(username, date, checkIn, checkOut);
    }


    @PostMapping("/admin/attendance/bulk")
    public List<ApiResponse<Object>> markBulkAttendance(@RequestBody List<Map<String, String>> bulkRequests) {
        return attendanceService.markBulkAttendance(bulkRequests);
    }

    @PostMapping("/admin/attendance/summary")
    public ResponseEntity<ApiResponse<List<AttendanceDayResponse>>> getAttendanceHistory(
            @RequestBody AttendanceRequest request) {

        List<AttendanceDayResponse> response = attendanceService.getAttendanceBetweenDates(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Attendance fetched successfully",
                response,
                HttpStatus.OK.value(),
                LocalDateTime.now()
        ));
    }
}
