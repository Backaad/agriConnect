package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KycStatusEvent {
    private UUID userId; private String status; private String reason;
    private LocalDateTime occurredAt;
}
