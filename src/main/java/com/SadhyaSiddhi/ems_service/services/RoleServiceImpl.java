package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.RoleNotFoundException;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repositories.RoleRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();

        if (roles.isEmpty()) {
            throw new RoleNotFoundException("No roles found in the system");
        }

        return roles;
    }

    @Override
    public List<String> getUserRoles(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();
    }

    @Override
    @Transactional
    public void updateUserRole(String username, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        userRepository.deleteRolesByUsername(username);
        userRepository.addRoleByUsername(username, role.getId());
    }



}
