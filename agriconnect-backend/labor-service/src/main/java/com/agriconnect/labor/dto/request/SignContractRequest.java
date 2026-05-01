package com.agriconnect.labor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignContractRequest {

    @NotBlank(message = "La signature numérique est obligatoire")
    private String signatureData; // Base64 de la signature dessinée

    private String deviceInfo; // Info appareil pour audit
}
