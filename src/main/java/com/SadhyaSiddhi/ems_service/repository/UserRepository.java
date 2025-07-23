package com.SadhyaSiddhi.ems_service.repository;

import com.SadhyaSiddhi.ems_service.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
