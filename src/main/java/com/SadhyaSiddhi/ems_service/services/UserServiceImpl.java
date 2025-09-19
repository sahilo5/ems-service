package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.dto.UserDto;
import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.repositories.RoleRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private RegisterDto registerDto;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserFullNameDto getUserFullName(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return new UserFullNameDto(user.getFirstName(), user.getLastName());
    }

    @Override
    public List<RegisterDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found");
        }

        return users.stream().map(user -> {
            RegisterDto dto = new RegisterDto();
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setPhoneNumber(user.getPhoneNumber());
            return dto;
        }).toList();
    }

    @Override
    public RegisterDto getUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        RegisterDto dto = new RegisterDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());

        return dto;
    }

    @Override
    public ApiResponse<Object> updateUserDetailsByUsername(String username, RegisterDto updateUserDto) {
        UserEntity existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Long userId = existingUser.getId();

        if(userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(),userId)){
            return new ApiResponse<>(false, "Email already exists",null);
        }
        if(userRepository.existsByPhoneNumberAndIdNot(updateUserDto.getPhoneNumber(),userId)){
            return new ApiResponse<>(false, "Phone number already exists",null);
        }



        int updated = userRepository.updateUserDetailsByUsername(
                updateUserDto.getUsername(),
                updateUserDto.getEmail(),
                updateUserDto.getFirstName(),
                updateUserDto.getLastName(),
                updateUserDto.getPhoneNumber()
        );
        if (updated == 0) {
            throw new UserNotFoundException(updateUserDto.getUsername());
        }
        return new ApiResponse<>(true, "User details updated successfully", null);
    }

    @Override
    public Boolean deleteUsers(List<String> usernames) {
        boolean allDeleted = true;
        for (String username : usernames) {
            try {
                UserEntity user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));

                user.getRoles().clear();
                userRepository.save(user);

                userRepository.delete(user);
            } catch (UserNotFoundException e) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    @Override
    public List<UserDto> getAllUsersWithRoles() {
        List<UserEntity> users = userRepository.findAllUsersWithRoles();

        return users.stream().map(user -> new UserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRoles().stream().map(Role::getName).toList()
        )).toList();
    }




}

