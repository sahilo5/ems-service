package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.BulkDeleteRequestDto;
import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.dto.UserDto;
import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    private RegisterDto registerDto;

    @GetMapping("/user/fullName/{username}")
    public ResponseEntity<UserFullNameDto> getUserFullName(@PathVariable String username) {
        UserFullNameDto fullNameDto = userService.getUserFullName(username);
        return ResponseEntity.ok(fullNameDto);
    }

    @GetMapping("/admin/getAllUsers")
    public ApiResponse<List<RegisterDto>> getAllUsers(){
        if(userService.getAllUsers().isEmpty()) {
            return new ApiResponse<>(false, "No users found", null);
        } else {
            return new ApiResponse<>(true, "Users retrieved successfully", userService.getAllUsers());
        }
    }

    @PostMapping ("/user/getUserByUsername/{username}")
    public ApiResponse<RegisterDto> getUserByUsername(@PathVariable String username){
        if(userService.getUserByUsername(username) == null) {
            return new ApiResponse<>(false, "User not found", null);
        } else {
            return new ApiResponse<>(true, "User retrieved successfully", userService.getUserByUsername(username));
        }
    }

    @PostMapping("/user/updateUser/{username}")
    public ApiResponse<RegisterDto> updateUser(@PathVariable String username, @RequestBody RegisterDto updateUserDto) {
        userService.updateUserDetailsByUsername(username, updateUserDto);
        if(!userService.updateUserDetailsByUsername(username, updateUserDto)) {
            return new ApiResponse<>(false, "User not found", null);
        } else {
            return new ApiResponse<>(true, "User updated successfully", null);
        }
    }

    @PostMapping("/admin/deleteUsers")
    public ApiResponse<String> deleteUsers(@RequestBody BulkDeleteRequestDto request) {
        boolean allDeleted = userService.deleteUsers(request.getUsers());
        if (allDeleted) {
            return new ApiResponse<>(true, "All users deleted successfully", null);
        } else {
            return new ApiResponse<>(false, "Some users could not be deleted", null);
        }
    }

    @GetMapping("/admin/getAllUsersWithRoles")
    public ApiResponse<List<UserDto>> getAllUsersWithRole() {
        return new ApiResponse<>(true, "Users fetched successfully", userService.getAllUsersWithRoles());
    }


}
