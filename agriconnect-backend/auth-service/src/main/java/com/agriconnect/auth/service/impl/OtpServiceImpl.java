package com.agriconnect.auth.service.impl;

import com.agriconnect.auth.service.OtpService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpServiceImpl implements OtpService {

    @Value("${twilio.account.sid:dummy}")
    private String accountSid;

    @Value("${twilio.auth.token:dummy}")
    private String authToken;

    @Value("${twilio.phone.number:+1234567890}")
    private String twilioPhoneNumber;

    // Temporary in-memory store for OTPs for demo purposes
    private final Map<String, String> otpStore = new HashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public void sendOtp(String phoneNumber) {
        String otp = String.format("%06d", secureRandom.nextInt(999999));
        otpStore.put(phoneNumber, otp);

        if (!"dummy".equals(accountSid)) {
            Twilio.init(accountSid, authToken);
            Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "Your AgriConnect OTP code is: " + otp
            ).create();
        } else {
            // Log for dev/test
            System.out.println("Dummy Twilio sending OTP " + otp + " to " + phoneNumber);
        }
    }

    @Override
    public boolean verifyOtp(String phoneNumber, String otp) {
        String storedOtp = otpStore.get(phoneNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(phoneNumber);
            return true;
        }
        return false;
    }
}
