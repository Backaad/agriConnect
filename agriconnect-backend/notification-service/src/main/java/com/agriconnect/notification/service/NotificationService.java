package com.agriconnect.notification.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.notification.dto.response.NotificationCountResponse;
import com.agriconnect.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    PageResponse<NotificationResponse> getMyNotifications(UUID userId, boolean unreadOnly, Pageable pageable);
    NotificationCountResponse getCounts(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    int markAllAsRead(UUID userId);
    void delete(UUID notificationId, UUID userId);
}
