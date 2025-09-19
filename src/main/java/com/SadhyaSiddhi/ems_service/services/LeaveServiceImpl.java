package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.LeaveResponseDto;
import com.SadhyaSiddhi.ems_service.models.*;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.repositories.AttendanceRepository;
import com.SadhyaSiddhi.ems_service.repositories.LeaveRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private  SmsService smsService;

    // Employee applies for leave
    public ApiResponse<Object> applyLeave(LeaveRequest leaveRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        leaveRequest.setUser(user);
        leaveRequest.setNoOfDays(leaveRequest.getDates().size());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setCreatedAt(LocalDate.now());

        leaveRepository.save(leaveRequest);

        return new ApiResponse<>(true, "Leave applied successfully", leaveRequest.getLeaveId(),
                HttpStatus.CREATED.value(), LocalDateTime.now());
    }

    // Employee views own leaves
    public ApiResponse<Object> getMyLeaves() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<LeaveRequest> leaves = leaveRepository.findByUser(user);

        List<LeaveResponseDto> leaveDtos = leaves.stream().map(leave -> {
            LeaveResponseDto dto = new LeaveResponseDto();
            dto.setId(leave.getLeaveId());
            dto.setStatus(leave.getStatus());
            dto.setReason(leave.getReason());
            dto.setDates(leave.getDates());
            dto.setType(leave.getType());
            dto.setNoOfDays(leave.getNoOfDays());
            dto.setDescription(leave.getDescription());
            dto.setCreatedAt(leave.getCreatedAt());
            dto.setRejectionMessage(leave.getRejectionMessage());
            return dto;
        }).toList();

        return new ApiResponse<>(true, "Fetched employee leaves", leaveDtos,
                HttpStatus.OK.value(), LocalDateTime.now());
    }

    // Employee updates own leave
    public ApiResponse<Object> updateLeave(Long leaveId, LeaveRequest updatedLeave) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!leave.getUser().getId().equals(user.getId())) {
            return new ApiResponse<>(false, "Not allowed to update others' leaves",
                    null, HttpStatus.FORBIDDEN.value(), LocalDateTime.now());
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            return new ApiResponse<>(false, "Cannot update approved/rejected leaves",
                    null, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        }

        leave.setReason(updatedLeave.getReason());
        leave.setType(updatedLeave.getType());
        leave.setDates(updatedLeave.getDates());
        leave.setNoOfDays(updatedLeave.getDates().size());
        leave.setDescription(updatedLeave.getDescription());

        leaveRepository.save(leave);

        return new ApiResponse<>(true, "Leave updated successfully", leave.getLeaveId(),
                HttpStatus.OK.value(), LocalDateTime.now());
    }

    // Admin: Get all leaves
    public ApiResponse<Object> getAllLeaves() {
        List<LeaveRequest> allLeaves = leaveRepository.findAll();

        List<LeaveResponseDto> leaveDtos = allLeaves.stream().map(leave -> {
            LeaveResponseDto dto = new LeaveResponseDto();
            dto.setId(leave.getLeaveId());
            dto.setStatus(leave.getStatus());
            dto.setReason(leave.getReason());
            dto.setDates(leave.getDates());
            dto.setType(leave.getType());
            dto.setNoOfDays(leave.getNoOfDays());
            dto.setDescription(leave.getDescription());
            dto.setCreatedAt(leave.getCreatedAt());
            dto.setRejectionMessage(leave.getRejectionMessage());

            dto.setEmployeeName(leave.getUser().getFirstName()+" "+leave.getUser().getLastName());
            return dto;
        }).toList();

        return new ApiResponse<>(true, "Fetched all leaves", leaveDtos,
                HttpStatus.OK.value(), LocalDateTime.now());
    }

    // Admin: Get leaves between two dates
    public ApiResponse<Object> getLeavesBetween(LocalDate start, LocalDate end) {
        List<LeaveRequest> leaves = leaveRepository.findByDatesBetween(start, end);

        List<LeaveResponseDto> leaveDtos = leaves.stream().map(leave -> {
            LeaveResponseDto dto = new LeaveResponseDto();
            dto.setId(leave.getLeaveId());
            dto.setStatus(leave.getStatus());
            dto.setReason(leave.getReason());
            dto.setDates(leave.getDates());
            dto.setType(leave.getType());
            dto.setNoOfDays(leave.getNoOfDays());
            dto.setDescription(leave.getDescription());
            dto.setCreatedAt(leave.getCreatedAt());
            dto.setRejectionMessage(leave.getRejectionMessage());
            return dto;
        }).toList();

        return new ApiResponse<>(true, "Fetched leaves between dates", leaveDtos,
                HttpStatus.OK.value(), LocalDateTime.now());
    }

    @Transactional
    public ApiResponse<Object> updateLeaveStatus(Long leaveId, LeaveStatus status, String rejectionMsg) {
        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(status);
        if (status == LeaveStatus.REJECTED) {
            leave.setRejectionMessage(rejectionMsg);
        }

        // Save leave update first
        leaveRepository.save(leave);

        // If approved → create attendance entries
        if (status == LeaveStatus.APPROVED) {
            UserEntity user = leave.getUser();

            for (LocalDate leaveDate : leave.getDates()) {
                // Check if attendance already exists for this day & user
                boolean exists = attendanceRepository.existsByUserAndDate(user, leaveDate);
                if (!exists) {
                    Attendance attendance = new Attendance();
                    attendance.setUser(user);
                    attendance.setDate(leaveDate);
                    attendance.setCheckInTime(null);
                    attendance.setCheckOutTime(null);
                    attendance.setStatus(AttendanceStatus.LEAVE);
                    attendance.setMarkedBy(MarkedBy.ADMIN);

                    attendanceRepository.save(attendance);
                }
            }
            smsService.sendSms(Long.toString(user.getPhoneNumber()), "My Love ♡, " + user.getUsername() + ", your leave has been Denied, I haven't done yet \uD83E\uDD2D. ");
        }

        return new ApiResponse<>(true, "Leave status updated", leave.getLeaveId(),
                HttpStatus.OK.value(), LocalDateTime.now());
    }


    public ApiResponse<Object> deleteLeave(Long leaveId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!leave.getUser().getId().equals(user.getId())) {
            return new ApiResponse<>(false, "Not allowed to delete others' leaves",
                    null, HttpStatus.FORBIDDEN.value(), LocalDateTime.now());
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            return new ApiResponse<>(false, "Cannot delete approved/rejected leaves",
                    null, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        }

        leave.getDates().clear();
        leaveRepository.save(leave);
        leaveRepository.delete(leave);

        return new ApiResponse<>(true, "Leave deleted successfully", leaveId,
                HttpStatus.OK.value(), LocalDateTime.now());
    }
}
