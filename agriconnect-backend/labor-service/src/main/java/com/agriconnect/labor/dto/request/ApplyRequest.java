package com.agriconnect.labor.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplyRequest {

    @Size(max = 500, message = "La note de candidature ne peut pas dépasser 500 caractères")
    private String coverNote;
}
