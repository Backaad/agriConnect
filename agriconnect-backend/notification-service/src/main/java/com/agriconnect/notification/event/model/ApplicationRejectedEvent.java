package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApplicationRejectedEvent {
    private  UUID applicationId;
    private UUID jobId;
    private UUID workerId;
    private String jobTitle;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
