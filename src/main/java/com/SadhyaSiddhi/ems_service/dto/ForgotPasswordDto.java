package com.SadhyaSiddhi.ems_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordDto {

    private String token;
    private String message;

}
