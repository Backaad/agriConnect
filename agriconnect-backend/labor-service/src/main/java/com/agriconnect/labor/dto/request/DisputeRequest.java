package com.agriconnect.labor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DisputeRequest {

    @NotBlank(message = "La raison du litige est obligatoire")
    @Size(min = 20, max = 1000)
    private String reason;
}
