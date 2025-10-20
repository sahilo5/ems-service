package com.SadhyaSiddhi.ems_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OtherPaymentsSummaryDto {
    private List<OtherPaymentDto> otherPayments;
    private double totalAmount;
}
