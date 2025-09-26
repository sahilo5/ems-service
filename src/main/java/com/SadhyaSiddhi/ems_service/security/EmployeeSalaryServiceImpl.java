package com.SadhyaSiddhi.ems_service.services.impl;

import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryConfigRepository;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryLogRepository;
import com.SadhyaSiddhi.ems_service.services.EmployeeSalaryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeSalaryServiceImpl implements EmployeeSalaryService {

    private final EmployeeSalaryConfigRepository configRepository;
    private final EmployeeSalaryLogRepository logRepository;

    // -------- Salary Config ----------
    @Override
    public EmployeeSalaryConfig createConfig(EmployeeSalaryConfig config) {
        return configRepository.save(config);
    }

    @Override
    public EmployeeSalaryConfig updateConfig(Long id, EmployeeSalaryConfig updated) {
        EmployeeSalaryConfig existing = configRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Config not found with id: " + id));
        existing.setSalaryTier(updated.getSalaryTier());
        existing.setBaseAmount(updated.getBaseAmount());
        existing.setActive(updated.isActive());
        return configRepository.save(existing);
    }

    @Override
    public void deleteConfig(Long id) {
        configRepository.deleteById(id);
    }

    @Override
    public EmployeeSalaryConfig getConfigById(Long id) {
        return configRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Config not found with id: " + id));
    }

    @Override
    public List<EmployeeSalaryConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    // -------- Salary Log ----------
    @Override
    public EmployeeSalaryLog createLog(EmployeeSalaryLog log, Long configId) {
        EmployeeSalaryConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new EntityNotFoundException("Config not found with id: " + configId));
        log.setConfig(config);
        return logRepository.save(log);
    }

    @Override
    public EmployeeSalaryLog updateLog(Long id, EmployeeSalaryLog updated) {
        EmployeeSalaryLog existing = logRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Log not found with id: " + id));

        existing.setAmountPaid(updated.getAmountPaid());
        existing.setStatus(updated.getStatus());
        existing.setPayDay(updated.getPayDay());
        existing.setRemarks(updated.getRemarks());

        return logRepository.save(existing);
    }

    @Override
    public void deleteLog(Long id) {
        logRepository.deleteById(id);
    }

    @Override
    public EmployeeSalaryLog getLogById(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Log not found with id: " + id));
    }

    @Override
    public List<EmployeeSalaryLog> getLogsByConfig(Long configId) {
        return logRepository.findByConfigId(configId);
    }

    @Override
    public List<EmployeeSalaryLog> getAllLogs() {
        return logRepository.findAll();
    }
}
