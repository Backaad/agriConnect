package com.agriconnect.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpSendRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phone;

    private String channel = "SMS"; // SMS ou WHATSAPP
}
