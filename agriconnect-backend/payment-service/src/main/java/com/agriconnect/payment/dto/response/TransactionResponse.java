package com.agriconnect.payment.dto.response;

import com.agriconnect.payment.domain.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private UUID id;
    private String reference;
    private TransactionType type;
    private String typeLabel;
    private Long amountFcfa;
    private Long feeFcfa;
    private TransactionStatus status;
    private String statusLabel;
    private PaymentProvider provider;
    private String description;
    private LocalDateTime createdAt;
}
