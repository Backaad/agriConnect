package com.agriconnect.notification.dto.request;

import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.domain.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class SendNotificationRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private NotificationType type;

    private NotificationChannel channel = NotificationChannel.PUSH;

    private Map<String, String> data;

    private String imageUrl;

    private String actionUrl;
}
