package com.agriconnect.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpVerifyRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phone;

    @NotBlank(message = "Le code OTP est obligatoire")
    @Size(min = 6, max = 6, message = "Le code OTP doit contenir exactement 6 chiffres")
    private String code;
}
