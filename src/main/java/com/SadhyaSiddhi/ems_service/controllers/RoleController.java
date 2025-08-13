package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.RoleService;
import com.SadhyaSiddhi.ems_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/admin/getRoles")
    public ApiResponse<List<String>> getRoles() {
        List<String> roleNames = roleService.getAllRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return new ApiResponse<>(true, "Roles fetched successfully", roleNames);
    }

    @GetMapping("/admin/getUserRoles/{username}")
    public ApiResponse<List<String>> getUserRoles(@PathVariable String username) {
        List<String> roles = roleService.getUserRoles(username);
        return new ApiResponse<>(true, "Current roles fetched successfully", roles);
    }

    @PostMapping("/admin/updateUserRole/{username}/{roleName}")
    public ApiResponse<String> updateUserRole(
            @PathVariable String username,
            @PathVariable String roleName) {
        roleService.updateUserRole(username, roleName);
        return new ApiResponse<>(true, "Role updated successfully", null);
    }
}
