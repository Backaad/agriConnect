package com.agriconnect.payment.event.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EscrowLockedEvent {
    private UUID escrowId;
    private UUID referenceId;
    private String referenceType;
    private UUID payerId;
    private UUID payeeId;
    private Long amountFcfa;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
