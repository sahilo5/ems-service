package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.models.AppSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Autowired
    private AppSettingService appSettingService;

    @Bean
    public JavaMailSender customJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();


        try {
            ObjectMapper mapper = new ObjectMapper();

            AppSetting emailSetting = appSettingService.getSetting(7L); // e.g. key=mail_username
            AppSetting passwordSetting = appSettingService.getSetting(8L); // e.g. key=mail_password

            String username = mapper.readTree(emailSetting.getData()).get("value").asText();
            String password = mapper.readTree(passwordSetting.getData()).get("value").asText();

            mailSender.setUsername(username);
            mailSender.setPassword(password);

        } catch (Exception e) {
            throw new RuntimeException("Email settings not configured properly: " + e.getMessage(), e);
        }

        // These remain static (from properties)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        return mailSender;
    }
}
