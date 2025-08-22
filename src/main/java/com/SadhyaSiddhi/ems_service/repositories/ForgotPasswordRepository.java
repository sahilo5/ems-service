package com.SadhyaSiddhi.ems_service.repositories;

import com.SadhyaSiddhi.ems_service.models.ForgotPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Integer> {

    // Java
    @Query("SELECT fp FROM ForgotPassword fp WHERE fp.user.id = ?1 AND fp.otp = ?2")
    Optional<ForgotPassword> findOtpAndUsername(Long user_id, Integer otp);

    @Modifying
    @Transactional
    @Query("DELETE FROM ForgotPassword fp WHERE fp.user.username = :username")
    void deleteByUsername(@Param("username") String username);

    @Query("SELECT fp FROM ForgotPassword fp WHERE fp.user.id = ?1")
    Optional<ForgotPassword> findByUserId(Long user_id);
}
