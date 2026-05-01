package com.agriconnect.payment.dto.request;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TopUpRequest {

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 100, message = "Montant minimum 100 FCFA")
    @Max(value = 5000000, message = "Montant maximum 5 000 000 FCFA")
    private Long amountFcfa;

    @NotNull(message = "Le provider est obligatoire")
    private PaymentProvider provider;

    @NotBlank(message = "Le numéro Mobile Money est obligatoire")
    @Pattern(regexp = "\\+?237[6-9]\\d{8}", message = "Numéro Mobile Money camerounais invalide")
    private String mobileNumber;

    private String idempotencyKey;
}
