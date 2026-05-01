package com.agriconnect.notification.domain.enums;
public enum NotificationChannel {
    PUSH,     // Firebase Cloud Messaging
    SMS,      // Twilio SMS
    EMAIL,    // SendGrid
    IN_APP    // Stocké uniquement en base (lecture via API)
}
