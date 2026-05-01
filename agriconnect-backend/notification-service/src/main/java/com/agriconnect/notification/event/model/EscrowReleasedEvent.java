package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EscrowReleasedEvent {
    private  UUID escrowId;
    private UUID referenceId;
    private UUID payerId;
    private UUID payeeId   ;
    private Long amountFcfa;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
