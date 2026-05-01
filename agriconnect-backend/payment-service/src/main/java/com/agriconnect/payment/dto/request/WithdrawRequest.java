package com.agriconnect.payment.dto.request;

import com.agriconnect.payment.domain.enums.PaymentProvider;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class WithdrawRequest {

    @NotNull
    @Min(value = 500, message = "Montant minimum de retrait : 500 FCFA")
    @Max(value = 2000000, message = "Montant maximum de retrait : 2 000 000 FCFA")
    private Long amountFcfa;

    @NotNull
    private PaymentProvider provider;

    @NotBlank
    @Pattern(regexp = "\\+?237[6-9]\\d{8}")
    private String mobileNumber;
}
