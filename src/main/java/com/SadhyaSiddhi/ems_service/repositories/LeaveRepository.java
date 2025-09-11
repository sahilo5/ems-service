package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.LeaveRequest;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUser(UserEntity user);
    List<LeaveRequest> findByDatesBetween(LocalDate start, LocalDate end);
}
