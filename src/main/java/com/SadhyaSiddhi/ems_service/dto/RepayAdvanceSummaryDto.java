package com.SadhyaSiddhi.ems_service.dto;

import java.util.List;

public class RepayAdvanceSummaryDto {
    private List<AdvanceDto> pendingAdvances;
    private double totalRemainingAmount;

    public RepayAdvanceSummaryDto() {}

    public RepayAdvanceSummaryDto(List<AdvanceDto> pendingAdvances, double totalRemainingAmount) {
        this.pendingAdvances = pendingAdvances;
        this.totalRemainingAmount = totalRemainingAmount;
    }

    public List<AdvanceDto> getPendingAdvances() {
        return pendingAdvances;
    }

    public void setPendingAdvances(List<AdvanceDto> pendingAdvances) {
        this.pendingAdvances = pendingAdvances;
    }

    public double getTotalRemainingAmount() {
        return totalRemainingAmount;
    }

    public void setTotalRemainingAmount(double totalRemainingAmount) {
        this.totalRemainingAmount = totalRemainingAmount;
    }
}

