package com.agriconnect.payment.dto.response;

import com.agriconnect.payment.domain.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WithdrawalResponse {
    private UUID id;
    private Long amountFcfa;
    private Long feeFcfa;
    private Long netAmountFcfa;
    private PaymentProvider provider;
    private String maskedMobileNumber;
    private WithdrawalStatus status;
    private LocalDateTime createdAt;
}
