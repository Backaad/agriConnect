package com.agriconnect.notification.dto.response;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationCountResponse {
    private long unreadCount;
    private long totalCount;
}
