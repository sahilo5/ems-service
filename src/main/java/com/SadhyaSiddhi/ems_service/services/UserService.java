package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;

public interface UserService {
    public UserFullNameDto getUserFullName(String username);
}
