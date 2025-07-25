package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.AuthResponseDto;
import com.SadhyaSiddhi.ems_service.dto.LoginDto;
import com.SadhyaSiddhi.ems_service.dto.RegisterDto;
import com.SadhyaSiddhi.ems_service.models.Role;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repository.RoleRepository;
import com.SadhyaSiddhi.ems_service.repository.UserRepository;
import com.SadhyaSiddhi.ems_service.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator= jwtGenerator;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        String userRole = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("USER");

        return new ResponseEntity<>(new AuthResponseDto(token,loginDto.getUsername(), userRole), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> regesterUser(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPhoneNumber(registerDto.getPhoneNumber());

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
