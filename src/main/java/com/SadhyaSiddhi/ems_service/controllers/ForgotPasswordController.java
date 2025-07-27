package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.MailBodyDto;
import com.SadhyaSiddhi.ems_service.dto.ResetPasswordDto;
import com.SadhyaSiddhi.ems_service.exceptions.CustomAuthenticationException;
import com.SadhyaSiddhi.ems_service.models.ForgotPassword;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repository.ForgotPasswordRepository;
import com.SadhyaSiddhi.ems_service.repository.UserRepository;
import com.SadhyaSiddhi.ems_service.security.JWTGenerator;
import com.SadhyaSiddhi.ems_service.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class ForgotPasswordController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/forgotPassword/{username}")
    public ResponseEntity<String> forgotPassword(@PathVariable  String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        int otp = generateOtp();

        MailBodyDto mailBody = MailBodyDto.builder()
                .to(user.getEmail())
                .subject("Password Reset OTP")
                .body("Your OTP for password reset is: " + otp)
                .build();

        ForgotPassword forgotPassword =  ForgotPassword.builder()
                .user(user)
                .otp(otp)
                .ExpirationTime(new Date(System.currentTimeMillis() + (1000 * 60 * 5))) // OTP valid for 5 minutes
                .build();

        emailService.sendSimpleEmail(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("OTP sent to your email successfully.");
    }

    @PostMapping("/verifyOtp/{username}/{otp}")
    private ResponseEntity<String> verifyOtp(@PathVariable  String username,@PathVariable Integer otp) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        ForgotPassword forgotPassword = forgotPasswordRepository.findOtpAndUsername(user.getId(), otp)
                .orElseThrow(() -> new CustomAuthenticationException("Invalid OTP for user: " + username));

        if (forgotPassword.getExpirationTime().before(Date.from(java.time.Instant.now()))) {
            forgotPasswordRepository.deleteByUsername(user.getUsername());
            return ResponseEntity.badRequest().body("OTP has expired. Please request a new OTP.");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        String jwtToken = jwtGenerator.generateToken(authToken);

        forgotPasswordRepository.deleteByUsername(user.getUsername());
        return ResponseEntity.ok( jwtToken );
    }

    @PostMapping("/resetPassword/{username}")
    public ResponseEntity<String> resetPassword(@PathVariable String username,@RequestBody ResetPasswordDto resetPasswordDto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successfully.");
    }

    @PostMapping("/resendOtp/{username}")
    public ResponseEntity<String> resendOtp(@PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        // Check if an OTP already exists for the user
        ForgotPassword existingForgotPassword = forgotPasswordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomAuthenticationException("No OTP found for user: " + username));

        forgotPasswordRepository.deleteByUsername(user.getUsername());
        return forgotPassword(username);
    }

    private Integer generateOtp() {
        return (int) (Math.random() * 9000) + 1000; // Generates a 4-digit OTP
    }
}
