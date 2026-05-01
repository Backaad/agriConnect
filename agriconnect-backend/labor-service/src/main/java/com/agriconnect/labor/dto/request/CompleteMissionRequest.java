package com.agriconnect.labor.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CompleteMissionRequest {

    @NotNull(message = "La note est obligatoire")
    @Min(1) @Max(5)
    private Short rating;

    @Size(max = 500)
    private String review;
}
