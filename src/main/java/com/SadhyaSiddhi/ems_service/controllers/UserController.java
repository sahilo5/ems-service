package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.UserFullNameDto;
import com.SadhyaSiddhi.ems_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/fullName/{username}")
    public ResponseEntity<UserFullNameDto> getUserFullName(@PathVariable String username) {
        UserFullNameDto fullNameDto = userService.getUserFullName(username);
        return ResponseEntity.ok(fullNameDto);
    }

}
