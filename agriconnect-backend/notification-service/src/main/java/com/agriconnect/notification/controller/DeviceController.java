package com.agriconnect.notification.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.notification.dto.request.RegisterDeviceRequest;
import com.agriconnect.notification.security.SecurityUtils;
import com.agriconnect.notification.service.DeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "Enregistrement des tokens FCM pour les notifications push")
public class DeviceController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Enregistrer / mettre à jour le token FCM de l'appareil",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request) {
        deviceTokenService.registerToken(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Appareil enregistré pour les notifications push"));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Désinscrire tous les appareils (à la déconnexion)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> unregisterAll() {
        deviceTokenService.unregisterAllUserDevices(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Tous les appareils désincrits"));
    }

    @DeleteMapping("/{token}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Désinscrire un appareil spécifique",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> unregisterDevice(@PathVariable String token) {
        deviceTokenService.unregisterToken(SecurityUtils.getCurrentUserId(), token);
        return ResponseEntity.ok(ApiResponse.success(null, "Appareil désinscrit"));
    }
}
