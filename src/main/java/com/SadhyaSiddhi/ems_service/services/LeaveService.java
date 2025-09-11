package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.models.*;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;

import java.time.LocalDate;

public interface LeaveService {

    public ApiResponse<Object> applyLeave(LeaveRequest leaveRequest);

    public ApiResponse<Object> getMyLeaves();

    public ApiResponse<Object> updateLeave(Long leaveId, LeaveRequest updatedLeave);

    public ApiResponse<Object> getAllLeaves();

    public ApiResponse<Object> getLeavesBetween(LocalDate start, LocalDate end);

    public ApiResponse<Object> updateLeaveStatus(Long leaveId, LeaveStatus status, String rejectionMsg);

    public ApiResponse<Object> deleteLeave(Long leaveId);
}
