package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;

import java.util.List;

public interface EmployeeService {

    public List<RegisterDto> getAllEmployees();
}
