package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;

import java.util.List;

public interface EmployeeSalaryService {

    // -------- Salary Config (Master) ----------
    EmployeeSalaryConfig createConfig(EmployeeSalaryConfig config);
    EmployeeSalaryConfig updateConfig(Long id, EmployeeSalaryConfig config);
    void deleteConfig(Long id);
    EmployeeSalaryConfig getConfigById(Long id);
    List<EmployeeSalaryConfig> getAllConfigs();

    // -------- Salary Log (History) ----------
    EmployeeSalaryLog createLog(EmployeeSalaryLog log, Long configId);
    EmployeeSalaryLog updateLog(Long id, EmployeeSalaryLog log);
    void deleteLog(Long id);
    EmployeeSalaryLog getLogById(Long id);
    List<EmployeeSalaryLog> getLogsByConfig(Long configId);
    List<EmployeeSalaryLog> getAllLogs();
}
