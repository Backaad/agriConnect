package com.agriconnect.notification.dto.response;

import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.domain.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    private UUID id;
    private String title;
    private String body;
    private NotificationType type;
    private NotificationChannel channel;
    private boolean isRead;
    private Map<String, String> data;
    private String imageUrl;
    private String actionUrl;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
