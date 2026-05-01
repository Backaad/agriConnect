package com.agriconnect.notification.event.model;
import lombok.*; import java.time.LocalDateTime; import java.util.UUID;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentSuccessEvent {
    private  String transactionRef;
    private UUID walletId;
    private Long amountFcfa;
    private String type;
    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}
