package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderDeliveredEvent {
    private  UUID orderId;
    private UUID consumerId;
    private UUID farmerId;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
