package com.agriconnect.notification.service;

public interface SmsNotificationService {
    boolean sendSms(String toPhone, String message);
}
