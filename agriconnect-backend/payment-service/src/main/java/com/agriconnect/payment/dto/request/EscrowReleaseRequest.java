package com.agriconnect.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

import lombok.*;

import java.util.UUID;

@Data
@Builder             // <-- Il manque ceci pour que .builder() fonctionne
@NoArgsConstructor   // <-- Requis avec @Builder
@AllArgsConstructor
public class EscrowReleaseRequest {

    @NotNull
    private UUID referenceId;

    @NotBlank
    private String reason;
}
