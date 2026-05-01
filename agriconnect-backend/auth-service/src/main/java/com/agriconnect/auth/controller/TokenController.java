package com.agriconnect.auth.controller;

import com.agriconnect.auth.security.SecurityUtils;
import com.agriconnect.auth.service.TokenService;
import com.agriconnect.commons.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/tokens")
@RequiredArgsConstructor
@Tag(name = "Tokens", description = "Gestion des tokens de sécurité")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/revoke-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Révoquer tous les tokens (déconnexion de tous les appareils)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> revokeAll() {
        tokenService.revokeAllUserTokens(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Tous les appareils déconnectés"));
    }
}
