package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.LeaveRequest;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserAndActiveTrue(UserEntity user);
    List<LeaveRequest> findByDatesBetweenAndActiveTrue(LocalDate start, LocalDate end);
    @Query("SELECT l FROM LeaveRequest l JOIN FETCH l.user")
    List<LeaveRequest> findAllWithUser();

    @Query("SELECT l FROM LeaveRequest l JOIN l.user u WHERE l.active = true AND u.active = true")
    List<LeaveRequest> findAllActiveLeavesForActiveUsers();

}
