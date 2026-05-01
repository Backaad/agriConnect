package com.agriconnect.payment.event.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MissionCompletedEvent {
    private UUID missionId;
    private UUID contractId;
    private UUID farmerId;
    private UUID workerId;
    private Long amountFcfa;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
