package com.SadhyaSiddhi.ems_service.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String username;
    private String tokenType = "Bearer ";
    private String urserRole;

    public AuthResponseDto(String accessToken, String username, String userRole) {
        this.accessToken = accessToken;
        this.username = username;
        this.urserRole = userRole;
    }
}
