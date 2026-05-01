package com.agriconnect.payment.dto.response;

import com.agriconnect.payment.domain.enums.TransactionStatus;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentInitResponse {
    private String transactionReference;
    private TransactionStatus status;
    private String providerRef;
    private String redirectUrl;
    private String message;
    private Long amountFcfa;
}
