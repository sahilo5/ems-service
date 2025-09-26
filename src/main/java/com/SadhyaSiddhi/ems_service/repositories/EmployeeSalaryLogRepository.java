package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeSalaryLogRepository extends JpaRepository<EmployeeSalaryLog, Long> {
    List<EmployeeSalaryLog> findByConfigId(Long configId);
}
