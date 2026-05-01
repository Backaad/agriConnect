package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserRegisteredEvent {
    private UUID userId; private String phone; private String role;
    private LocalDateTime registeredAt;
}
