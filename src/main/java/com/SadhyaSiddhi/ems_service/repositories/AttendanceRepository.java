package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.Attendance;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Attendance a where a.user.id = :userId and a.date = :date")
    Optional<Attendance> findForUpdate(@Param("userId") Long userId, @Param("date") LocalDate date);

    Optional<Attendance> findByUserAndDate(UserEntity user, LocalDate date);
}
