package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.models.AppSetting;

import java.util.List;

public interface AppSettingService {
    public AppSetting getSetting(Long id);

    public AppSetting createSetting(AppSetting setting);

    public AppSetting updateSetting(Long id, AppSetting newSetting);

    public void deleteSetting(Long id);

    public List<AppSetting> getAllSettings();

    List<AppSetting> getSettingsByCategory(String category);
}
