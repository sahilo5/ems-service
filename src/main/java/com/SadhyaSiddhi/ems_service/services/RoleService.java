package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.models.Role;

import java.util.List;

public interface RoleService {
    public List<Role> getAllRoles();

    public List<String> getUserRoles(String username);

    public void updateUserRole(String username, String roleName);
}
