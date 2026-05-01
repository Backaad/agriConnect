package com.agriconnect.payment.dto.request;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class InitiatePaymentRequest {

    @NotNull
    private UUID orderId;

    @NotBlank
    private String orderType;  // MARKETPLACE_ORDER | LABOR_CONTRACT

    @NotNull
    @Min(100)
    private Long amountFcfa;

    @NotNull
    private PaymentProvider provider;

    private String mobileNumber;

    private String idempotencyKey;
}
