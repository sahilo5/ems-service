package com.SadhyaSiddhi.ems_service.controllers;

import com.SadhyaSiddhi.ems_service.dto.ForgotPasswordDto;
import com.SadhyaSiddhi.ems_service.dto.MailBodyDto;
import com.SadhyaSiddhi.ems_service.dto.ResetPasswordDto;
import com.SadhyaSiddhi.ems_service.exceptions.CustomAuthenticationException;
import com.SadhyaSiddhi.ems_service.models.ForgotPassword;
import com.SadhyaSiddhi.ems_service.models.UserEntity;
import com.SadhyaSiddhi.ems_service.repositories.ForgotPasswordRepository;
import com.SadhyaSiddhi.ems_service.repositories.UserRepository;
import com.SadhyaSiddhi.ems_service.security.JWTGenerator;
import com.SadhyaSiddhi.ems_service.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    public ResponseEntity<ForgotPasswordDto> forgotPassword(@PathVariable  String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        int otp = generateOtp();

        MailBodyDto mailBody = MailBodyDto.builder()
                .to(user.getEmail())
                .subject("Reset password")
                .body("Hi, Otp for Verification is: " + otp)
                .build();

        ForgotPassword forgotPassword =  ForgotPassword.builder()
                .user(user)
                .otp(otp)
                .ExpirationTime(new Date(System.currentTimeMillis() + (1000 * 60 * 5))) // OTP valid for 5 minutes
                .build();

        emailService.sendSimpleEmail(mailBody);

        if (forgotPasswordRepository.findByUserId(user.getId()).isPresent()) {
            forgotPasswordRepository.deleteByUsername(user.getUsername());
        }
        forgotPasswordRepository.save(forgotPassword);

        ForgotPasswordDto forgotPasswordDto = ForgotPasswordDto.builder()
                .message("OTP sent to your email successfully.")
                .build();

        return ResponseEntity.ok( forgotPasswordDto );
    }

    @PostMapping("/verifyOtp/{username}/{otp}")
    private ResponseEntity<ForgotPasswordDto> verifyOtp(@PathVariable  String username,@PathVariable Integer otp) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        ForgotPassword forgotPassword = forgotPasswordRepository.findOtpAndUsername(user.getId(), otp)
                .orElseThrow(() -> new CustomAuthenticationException("Invalid OTP for user: " + username));

        if (forgotPassword.getExpirationTime().before(Date.from(java.time.Instant.now()))) {
            forgotPasswordRepository.deleteByUsername(user.getUsername());
            throw new CustomAuthenticationException("OTP has expired. Please request a new OTP.");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        String jwtToken = jwtGenerator.generateToken(authToken);

        ForgotPasswordDto forgotPasswordDto = ForgotPasswordDto.builder()
                .token(jwtToken)
                .build();
        forgotPasswordRepository.deleteByUsername(user.getUsername());
        return ResponseEntity.ok( forgotPasswordDto );
    }

    @PostMapping("/resetPassword/{username}")
    public ResponseEntity<ForgotPasswordDto> resetPassword(@PathVariable String username,@RequestBody ResetPasswordDto resetPasswordDto) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);

        ForgotPasswordDto forgotPasswordDto = ForgotPasswordDto.builder()
                .message("Password reset successfully.")
                .build();

        return ResponseEntity.ok(forgotPasswordDto);
    }

    @PostMapping("/resendOtp/{username}")
    public ResponseEntity<ForgotPasswordDto> resendOtp(@PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthenticationException("User not found with username: " + username));

        if (forgotPasswordRepository.findByUserId(user.getId()).isEmpty()) {
            return forgotPassword(username);
        }

        forgotPasswordRepository.deleteByUsername(user.getUsername());
        return forgotPassword(username);
    }

    private Integer generateOtp() {
        return (int) (Math.random() * 9000) + 1000; // Generates a 4-digit OTP
    }
}
