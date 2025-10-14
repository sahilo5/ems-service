package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.AdvanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvanceLogRepository extends JpaRepository<AdvanceLog, Long> {
    List<AdvanceLog> findByAdvanceId(Long advanceId);
}

