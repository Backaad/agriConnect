package com.agriconnect.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddRoleRequest {

    @NotBlank(message = "Le rôle est obligatoire")
    private String role;
}
