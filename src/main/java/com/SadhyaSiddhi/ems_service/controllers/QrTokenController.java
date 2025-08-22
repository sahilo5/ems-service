package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.QrTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrTokenController {

    private final QrTokenService qrTokenService;

    // Admin gets QR token
    @GetMapping("/generate")
    public ApiResponse<String> generateQrToken() {
        String token = qrTokenService.generateQrToken();
        return new ApiResponse<>(true, "QR token generated", token);
    }

    // Employee validates QR by scanning & submitting token
    @PostMapping("/validate")
    public ApiResponse<String> validateQrToken(@RequestParam String token) {
        qrTokenService.validateQrToken(token);
        // here you can call attendanceService.markAttendance(userId, date)
        return new ApiResponse<>(true, "QR token validated. Attendance marked!", null);
    }
}
