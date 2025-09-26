package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements  EmployeeService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RegisterDto> getAllEmployees() {
        List<UserEntity> employees = userRepository.findAllEmployees();
        if (employees.isEmpty()) {
            throw new UserNotFoundException("No users found");
        }

        return employees.stream().map(emp -> {
            RegisterDto dto = new RegisterDto();
            dto.setUsername(emp.getUsername());
            dto.setEmail(emp.getEmail());
            dto.setFirstName(emp.getFirstName());
            dto.setLastName(emp.getLastName());
            dto.setPhoneNumber(emp.getPhoneNumber());
            return dto;
        }).toList();
    }

    @Override
    public LocalDate getEmployeeSince(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return user.getEmployeeSince();
    }
}
