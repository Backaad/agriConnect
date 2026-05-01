package com.agriconnect.payment.event.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentFailedEvent {
    private String transactionRef;
    private UUID walletId;
    private Long amountFcfa;
    private String reason;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
