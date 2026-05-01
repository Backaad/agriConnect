package com.agriconnect.user.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.user.dto.request.UpdateSettingsRequest;
import com.agriconnect.user.dto.response.UserSettingsResponse;
import com.agriconnect.user.security.SecurityUtils;
import com.agriconnect.user.service.UserSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me/settings")
@RequiredArgsConstructor
@Tag(name = "User Settings", description = "Paramètres et préférences utilisateur")
public class UserSettingsController {

    private final UserSettingsService settingsService;

    @GetMapping
    @Operation(summary = "Récupérer mes paramètres", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserSettingsResponse>> getSettings() {
        return ResponseEntity.ok(ApiResponse.success(settingsService.getSettings(SecurityUtils.getCurrentUserId())));
    }

    @PutMapping
    @Operation(summary = "Mettre à jour mes paramètres", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserSettingsResponse>> updateSettings(@RequestBody UpdateSettingsRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                settingsService.updateSettings(SecurityUtils.getCurrentUserId(), req),
                "Paramètres mis à jour"));
    }
}
