package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.Attendance;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndDate(UserEntity user, LocalDate date);

    List<Attendance> findByUserAndDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);

    boolean existsByUserAndDate(UserEntity user, LocalDate date);

}
