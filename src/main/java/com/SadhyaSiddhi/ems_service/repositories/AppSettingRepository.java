package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    List<AppSetting> findByCategoryIgnoreCase(String category);
}
