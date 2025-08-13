package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.dto.UserDto;
import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;

import java.util.List;

public interface UserService {
    public UserFullNameDto getUserFullName(String username);

    public List<RegisterDto> getAllUsers();

    public RegisterDto getUserByUsername(String username);

    public Boolean updateUserDetailsByUsername(String username, RegisterDto updateUserDto);

    public Boolean deleteUsers(List<String>
                                       usernames);
    public List<UserDto> getAllUsersWithRoles();
}
