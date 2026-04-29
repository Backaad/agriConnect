package com.agriconnect.notification.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("${twilio.account.sid:dummy_sid}")
    private String accountSid;

    @Value("${twilio.auth.token:dummy_token}")
    private String authToken;

    @Value("${twilio.phone.number:+1234567890}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        if (!"dummy_sid".equals(accountSid)) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendSms(String to, String body) {
        if ("dummy_sid".equals(accountSid)) {
            System.out.println("Mock SMS sent to " + to + ": " + body);
            return;
        }

        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioPhoneNumber),
                body
        ).create();
    }
}
