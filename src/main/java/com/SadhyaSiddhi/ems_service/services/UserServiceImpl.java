package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.exceptions.CustomAuthenticationException;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.payload.ApiResponse;
import com.SadhyaSiddhi.ems_service.repository.RoleRepository;
import com.SadhyaSiddhi.ems_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
    public Boolean updateUserDetailsByUsername(String username, RegisterDto updateUserDto) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        int updated = userRepository.updateUserDetailsByUsername(
                updateUserDto.getUsername(),
                updateUserDto.getPassword(),
                updateUserDto.getEmail(),
                updateUserDto.getFirstName(),
                updateUserDto.getLastName(),
                updateUserDto.getPhoneNumber()
        );
        if (updated == 0) {
            throw new UserNotFoundException(updateUserDto.getUsername());
        }
        return true;
    }

    @Override
    public Boolean deleteUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        user.getRoles().clear();
        userRepository.save(user);

        userRepository.delete(user);
        return true;

    }

}

