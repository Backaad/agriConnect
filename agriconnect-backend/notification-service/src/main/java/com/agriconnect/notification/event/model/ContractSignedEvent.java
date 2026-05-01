package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContractSignedEvent {
    private  UUID contractId;
    private UUID farmerId;
    private UUID workerId;
    private Long amountFcfa;
    private String jobTitle;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
