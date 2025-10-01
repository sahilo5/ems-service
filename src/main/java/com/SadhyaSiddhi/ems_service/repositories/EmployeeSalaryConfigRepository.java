package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeSalaryConfigRepository extends JpaRepository<EmployeeSalaryConfig, Long> {
    Optional<EmployeeSalaryConfig> findByUser(UserEntity user);
}
