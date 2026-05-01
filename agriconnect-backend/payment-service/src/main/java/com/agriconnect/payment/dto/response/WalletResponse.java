package com.agriconnect.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletResponse {
    private UUID id;
    private UUID userId;
    private Long balanceFcfa;
    private Long frozenFcfa;
    private Long availableBalanceFcfa;
    private String currency;
    private String formattedBalance;
    private String formattedAvailable;
    private LocalDateTime updatedAt;
}
