package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.models.AppSetting;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.AppSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
public class AppSettingController {

    @Autowired
    private AppSettingService service;

    // 1. Get all settings
    @GetMapping("/getAllSettings")
    public ApiResponse<List<AppSetting>> getAllSettings() {
        return new ApiResponse<>(true, "All settings fetched successfully", service.getAllSettings());
    }

    // 2. Get particular setting by id
    @GetMapping("/{id}")
    public ApiResponse<AppSetting> getSetting(@PathVariable Long id) {
        return new ApiResponse<>(true, "Setting fetched successfully", service.getSetting(id));
    }

    // 3. Create new setting
    @PostMapping("/create")
    public ApiResponse<AppSetting> createSetting(@RequestBody AppSetting setting) {
        return new ApiResponse<>(true, "Setting created successfully", service.createSetting(setting));
    }

    // 4. Update setting
    @PutMapping("/update/{id}")
    public ApiResponse<AppSetting> updateSetting(@PathVariable Long id, @RequestBody AppSetting newSetting) {
        return new ApiResponse<>(true, "Setting updated successfully", service.updateSetting(id, newSetting));
    }

    // 5. Delete setting
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteSetting(@PathVariable Long id) {
        service.deleteSetting(id);
        return new ApiResponse<>(true, "Setting deleted successfully", null);
    }
}
