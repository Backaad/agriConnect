package com.agriconnect.auth.service;

public interface OtpService {
    void sendOtp(String phoneNumber);
    boolean verifyOtp(String phoneNumber, String otp);
}
