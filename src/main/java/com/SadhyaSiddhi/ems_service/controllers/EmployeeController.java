package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.EmployeeService;
import com.SadhyaSiddhi.ems_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/admin/getAllEmployees")
    public ApiResponse<List<RegisterDto>> getAllUsers(){
        if(employeeService.getAllEmployees().isEmpty()) {
            return new ApiResponse<>(false, "No Employees found", null);
        } else {
            return new ApiResponse<>(true, "Employees retrieved successfully", employeeService.getAllEmployees());
        }
    }

    @GetMapping("/admin/employee-since/{username}")
    public ApiResponse<LocalDate> getEmployeeSince(@PathVariable String username) {
        LocalDate sinceDate = employeeService.getEmployeeSince(username);
        return new ApiResponse<>(true, "Employee since date fetched successfully", sinceDate);
    }
}
