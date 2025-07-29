package com.SadhyaSiddhi.ems_service.repository;

import com.SadhyaSiddhi.ems_service.models.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.password = ?2, u.email = ?3, u.firstName = ?4, u.lastName = ?5, u.phoneNumber = ?6 WHERE u.username = ?1")
    int updateUserDetailsByUsername(String username, String password, String email, String firstName, String lastName, long phoneNumber);
}
