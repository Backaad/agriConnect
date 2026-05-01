package com.agriconnect.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String lastName;

    @Size(max = 500, message = "La bio ne peut pas dépasser 500 caractères")
    private String bio;

    @Size(max = 100)
    private String region;

    @Size(max = 100)
    private String city;

    private List<String> languages;

    private List<String> specialties;
}
