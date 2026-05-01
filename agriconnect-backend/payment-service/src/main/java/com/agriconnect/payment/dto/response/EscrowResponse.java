package com.agriconnect.payment.dto.response;

import com.agriconnect.payment.domain.enums.EscrowStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EscrowResponse {
    private UUID id;
    private UUID referenceId;
    private String referenceType;
    private UUID payerId;
    private UUID payeeId;
    private Long amountFcfa;
    private Long platformFee;
    private Long netPayeeAmount;
    private EscrowStatus status;
    private LocalDateTime lockedAt;
    private LocalDateTime releasedAt;
    private LocalDateTime expiresAt;
    private String releaseReason;
}
