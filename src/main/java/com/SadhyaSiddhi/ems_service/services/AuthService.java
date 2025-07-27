package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.AuthResponseDto;
import com.SadhyaSiddhi.ems_service.dto.LoginDto;
import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;

public interface AuthService {
    AuthResponseDto loginUser(LoginDto loginDto);

    ApiResponse<String> registerUser(RegisterDto registerDto);
}