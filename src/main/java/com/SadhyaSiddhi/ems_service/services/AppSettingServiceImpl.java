package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.ResourceNotFoundException;
import com.SadhyaSiddhi.ems_service.models.AppSetting;
import com.SadhyaSiddhi.ems_service.repositories.AppSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        existing.setKey(newSetting.getKey());
        existing.setTitle(newSetting.getTitle());
        existing.setDescription(newSetting.getDescription());
        existing.setCategory(newSetting.getCategory());
        existing.setDataType(newSetting.getDataType());
        existing.setData(newSetting.getData());

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
