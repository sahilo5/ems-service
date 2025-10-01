package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.RoleNotFoundException;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.EmployeeSalaryConfig;
import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repositories.EmployeeSalaryConfigRepository;
import com.SadhyaSiddhi.ems_service.repositories.RoleRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeSalaryConfigRepository configRepository;

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

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // remove old roles and assign new
        userRepository.deleteRolesByUsername(username);
        userRepository.addRoleByUsername(username, role.getId());

        if ("EMPLOYEE".equalsIgnoreCase(role.getName())) {
            if (user.getEmployeeSince() == null) {  // only set the first time
                user.setEmployeeSince(LocalDate.now());
                userRepository.save(user);
            }

            // create a blank salary config
            if(configRepository.findByUser(user).isEmpty()) {
            EmployeeSalaryConfig blankConfig = new EmployeeSalaryConfig();
            blankConfig.setUser(user);
            blankConfig.setActive(true);
            blankConfig.setSalaryTier(null);
            blankConfig.setBaseAmount(0.0);

            configRepository.save(blankConfig);
            }

        }
    }




}
