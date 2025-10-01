package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.ResourceNotFoundException;
import com.SadhyaSiddhi.ems_service.models.AppSetting;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryLog;
import com.SadhyaSiddhi.ems_service.repositories.AppSettingRepository;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryConfigRepository;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryLogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppSettingServiceImpl implements AppSettingService {

    @Autowired
    private AppSettingRepository repository;

    public AppSetting getSetting(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppSetting not found with id: " + id));
    }

    public AppSetting createSetting(AppSetting setting) {
        return repository.save(setting);
    }

    public AppSetting updateSetting(Long id, AppSetting newSetting) {
        AppSetting existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppSetting not found with id: " + id));

        if(newSetting.getKey()!=null){
            existing.setKey(newSetting.getKey());
        }
        if(newSetting.getTitle()!=null){
            existing.setTitle(newSetting.getTitle());
        }
        if(newSetting.getDescription()!=null){
            existing.setDescription(newSetting.getDescription());
        }
        if(newSetting.getCategory()!=null){
            existing.setCategory(newSetting.getCategory());
        }
        if(newSetting.getDataType()!=null){
            existing.setDataType(newSetting.getDataType());
        }
        if(newSetting.getData()!=null){
            existing.setData(newSetting.getData());
        }

        return repository.save(existing);
    }

    public void deleteSetting(Long id) {
        AppSetting existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AppSetting not found with id: " + id));
        repository.delete(existing);
    }

    public List<AppSetting> getAllSettings() {
        return repository.findAll();
    }

    @Override
    public List<AppSetting> getSettingsByCategory(String category) {
        List<AppSetting> settings = repository.findByCategoryIgnoreCase(category);
        if (settings.isEmpty()) {
            throw new ResourceNotFoundException("No settings found for category: " + category);
        }
        return settings;
    }
}
