package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.models.LeaveRequest;
import com.SadhyaSiddhi.ems_service.models.LeaveStatus;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.LeaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    // Employee: apply for leave
    @PostMapping("/user/leave/apply")
    public ResponseEntity<ApiResponse<Object>> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        return ResponseEntity.ok(leaveService.applyLeave(leaveRequest));
    }

    // Employee: view own leaves
    @GetMapping("/user/leave/my-leaves")
    public ResponseEntity<ApiResponse<Object>> getMyLeaves() {
        return ResponseEntity.ok(leaveService.getMyLeaves());
    }

    // Employee: update own leave
    @PutMapping("/user/leave/update/{id}")
    public ResponseEntity<ApiResponse<Object>> updateLeave(
            @PathVariable Long id, @RequestBody LeaveRequest updatedLeave) {
        return ResponseEntity.ok(leaveService.updateLeave(id, updatedLeave));
    }

    // Admin: get all leaves
    @GetMapping("/admin/leave/all")
    public ResponseEntity<ApiResponse<Object>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    // Admin: get leaves between dates
    @GetMapping("/admin/leave/between")
    public ResponseEntity<ApiResponse<Object>> getLeavesBetween(
            @RequestParam LocalDate start, @RequestParam LocalDate end) {
        return ResponseEntity.ok(leaveService.getLeavesBetween(start, end));
    }

    // Admin: approve/reject leave
    @PostMapping("/admin/leave/status/{id}")
    public ResponseEntity<ApiResponse<Object>> updateLeaveStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        LeaveStatus  leaveStatus= LeaveStatus.valueOf(requestBody.get("status"));
        String rejectionMsg = requestBody.get("rejectionMsg");
        return ResponseEntity.ok(leaveService.updateLeaveStatus(id, leaveStatus, rejectionMsg));
    }

    @PostMapping("/user/leave/delete/{leaveId}")
    public ResponseEntity<ApiResponse<Object>> deleteLeave(@PathVariable  Long leaveId){
         return ResponseEntity.ok(leaveService.deleteLeave(leaveId));
    }
}
