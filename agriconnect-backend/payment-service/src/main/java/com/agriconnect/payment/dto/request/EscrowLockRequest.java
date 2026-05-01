package com.agriconnect.payment.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import java.util.UUID;

import lombok.*;

@Data
@Builder             // <-- Il manque ceci pour que .builder() fonctionne
@NoArgsConstructor   // <-- Requis avec @Builder
@AllArgsConstructor
public class EscrowLockRequest {

    @NotNull
    private UUID referenceId;

    @NotBlank
    private String referenceType;

    @NotNull
    private UUID payerId;

    @NotNull
    private UUID payeeId;

    @NotNull
    @Min(100)
    private Long amountFcfa;
}
