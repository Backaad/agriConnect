package com.agriconnect.notification.service.impl;

import com.agriconnect.notification.service.SmsNotificationService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsNotificationServiceImpl implements SmsNotificationService {

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @Override
    public boolean sendSms(String toPhone, String message) {
        if (!twilioEnabled) {
            log.warn("[DEV] SMS simulé → to={} msg='{}'", toPhone, message);
            return true;
        }
        try {
            Message.creator(
                    new PhoneNumber(toPhone),
                    new PhoneNumber(fromNumber),
                    message
            ).create();
            log.info("SMS envoyé: to={}", toPhone);
            return true;
        } catch (Exception e) {
            log.error("SMS erreur: to={} err={}", toPhone, e.getMessage());
            return false;
        }
    }
}
