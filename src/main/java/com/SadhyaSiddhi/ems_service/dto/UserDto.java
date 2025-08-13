package com.SadhyaSiddhi.ems_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private List<String> roles;

}
