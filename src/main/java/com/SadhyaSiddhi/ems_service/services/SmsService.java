package com.SadhyaSiddhi.ems_service.services;

import com.SadhyaSiddhi.ems_service.exceptions.SettingNotConfiguredProperlyException;
import com.SadhyaSiddhi.ems_service.models.AppSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Autowired
    private AppSettingServiceImpl appSettingService;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Sends an SMS message using Twilio settings from the database.
     *
     * @param to      Recipient phone number (e.g. "+919876543210")
     * @param message Message text to send
     */
    public void sendSms(String to, String message) {
        try {
            // 1. Get Twilio SID
            AppSetting sidSetting = appSettingService.getSetting(9L); // key: twilio_sid
            String sid = mapper.readTree(sidSetting.getData()).get("value").asText();

            // 2. Get Twilio AuthToken
            AppSetting tokenSetting = appSettingService.getSetting(10L); // key: twilio_authToken
            String authToken = mapper.readTree(tokenSetting.getData()).get("value").asText();

            // 3. Get Twilio Phone
            AppSetting phoneSetting = appSettingService.getSetting(11L); // key: twilio_phone
            String fromPhone = mapper.readTree(phoneSetting.getData()).get("value").asText();

            if (sid == null || authToken == null || fromPhone == null) {
                throw new SettingNotConfiguredProperlyException("Twilio settings are not configured properly.");
            }

            // Initialize Twilio client
            Twilio.init(sid, authToken);

            // Send SMS
            Message.creator(
                    new PhoneNumber("+91"+to),
                    new PhoneNumber(fromPhone),
                    message
            ).create();

        } catch (Exception e) {
            throw new SettingNotConfiguredProperlyException("Error while sending SMS: " + e.getMessage());
        }
    }
}
