package com.SadhyaSiddhi.ems_service.dto;

import lombok.Builder;

@Builder
public record  MailBodyDto(String to, String subject, String body) {

    public MailBodyDto {
        if (to == null || subject == null || body == null) {
            throw new IllegalArgumentException("All fields must be provided");
        }
    }
}
