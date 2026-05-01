package com.agriconnect.payment.event.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderConfirmedEvent {
    private UUID orderId;
    private UUID consumerId;
    private UUID farmerId;
    private Long amountFcfa;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
