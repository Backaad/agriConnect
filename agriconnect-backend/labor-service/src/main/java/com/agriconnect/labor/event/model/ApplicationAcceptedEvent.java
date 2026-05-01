package com.agriconnect.labor.event.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApplicationAcceptedEvent {
    private UUID applicationId;
    private UUID jobId;
    private UUID farmerId;
    private UUID workerId;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
