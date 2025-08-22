package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Endpoint for Admin to generate rotating QR tokens
     */
    @GetMapping("/generate-qr")
    public ResponseEntity<ApiResponse<Object>> generateQr() {
        String qrToken = attendanceService.generateQrToken();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "QR Token generated", qrToken, 200, java.time.LocalDateTime.now())
        );
    }

    /**
     * Endpoint for Employees to scan QR and mark attendance
     */
    @PostMapping("/mark")
    public ApiResponse<Object> markAttendance(@RequestBody Map<String,String> requestBody) {
        Map <String, String> reqBody = requestBody;
        String qrToken = reqBody.get("token");
        String inOrOut = reqBody.get("inOrOut");
        ApiResponse<Object> response = attendanceService.markAttendance(qrToken, inOrOut);
        return response;
    }
}
