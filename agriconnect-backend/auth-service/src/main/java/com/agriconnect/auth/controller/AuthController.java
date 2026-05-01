package com.agriconnect.auth.controller;

import com.agriconnect.auth.dto.request.LoginRequest;
import com.agriconnect.auth.dto.request.RefreshTokenRequest;
import com.agriconnect.auth.dto.request.RegisterRequest;
import com.agriconnect.auth.dto.response.AuthResponse;
import com.agriconnect.auth.security.SecurityUtils;
import com.agriconnect.auth.service.AuthService;
import com.agriconnect.commons.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints d'inscription, connexion et gestion des tokens")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Compte créé avec succès. Vérifiez votre numéro de téléphone."));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion avec téléphone et mot de passe")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Connexion réussie"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renouveler le token d'accès")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "Token renouvelé"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(SecurityUtils.getCurrentUserId(), request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "Déconnexion réussie"));
    }

    @GetMapping("/validate")
    @Operation(summary = "Valider un token JWT (usage interne microservices)")
    public ResponseEntity<ApiResponse<Void>> validate() {
        return ResponseEntity.ok(ApiResponse.success(null, "Token valide"));
    }
}
