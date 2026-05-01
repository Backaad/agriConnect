package com.agriconnect.notification.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Slf4j @Configuration
public class TwilioConfig {
    @Value("${twilio.account-sid}") private String accountSid;
    @Value("${twilio.auth-token}")  private String authToken;
    @Value("${twilio.enabled:false}") private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled) { Twilio.init(accountSid, authToken); log.info("Twilio initialisé"); }
        else log.warn("Twilio désactivé (dev mode)");
    }
}
