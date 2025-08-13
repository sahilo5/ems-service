package com.SadhyaSiddhi.ems_service.repository;

import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.email = ?2, u.firstName = ?3, u.lastName = ?4, u.phoneNumber = ?5 WHERE u.username = ?1")
    int updateUserDetailsByUsername(String username, String email, String firstName, String lastName, long phoneNumber);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_roles ur " +
            "WHERE ur.user_id = (SELECT id FROM users WHERE username = :username)", nativeQuery = true)
    void deleteRolesByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) " +
            "VALUES ((SELECT id FROM users WHERE username = :username), :roleId)", nativeQuery = true)
    void addRoleByUsername(@Param("username") String username, @Param("roleId") Long roleId);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles")
    List<UserEntity> findAllUsersWithRoles();

}
