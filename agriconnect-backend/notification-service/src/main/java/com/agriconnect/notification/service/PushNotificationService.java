package com.agriconnect.notification.service;

import java.util.List;
import java.util.UUID;

public interface PushNotificationService {
    boolean sendToUser(UUID userId, String title, String body, java.util.Map<String, String> data);
    boolean sendToToken(String token, String title, String body, java.util.Map<String, String> data);
    void sendToMultipleUsers(List<UUID> userIds, String title, String body);
}
