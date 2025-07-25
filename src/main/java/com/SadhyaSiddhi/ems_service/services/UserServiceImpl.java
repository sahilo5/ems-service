package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.exceptions.UserNotFoundException;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserFullNameDto getUserFullName(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return new UserFullNameDto(user.getFirstName(), user.getLastName());
    }

}

