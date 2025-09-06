package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
}
