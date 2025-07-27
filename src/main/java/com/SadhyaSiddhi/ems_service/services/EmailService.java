package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.dto.MailBodyDto;
import com.SadhyaSiddhi.ems_service.exceptions.CustomAuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailSenderUsername;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendSimpleEmail(MailBodyDto mailBodyDto) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBodyDto.to());
        message.setFrom((String)mailSenderUsername);
        message.setSubject(mailBodyDto.subject());
        message.setText(mailBodyDto.body());

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new CustomAuthenticationException("Failed to send email: " + e.getMessage(), e);
        }

    }
}
