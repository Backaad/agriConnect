package com.agriconnect.user.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.user.dto.request.UpdateProfileRequest;
import com.agriconnect.user.dto.response.PublicProfileResponse;
import com.agriconnect.user.dto.response.UserProfileResponse;
import com.agriconnect.user.security.SecurityUtils;
import com.agriconnect.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Gestion des profils utilisateurs")
public class UserProfileController {

    private final UserProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Récupérer mon profil", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(profileService.getMyProfile(SecurityUtils.getCurrentUserId())));
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre à jour mon profil", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                profileService.updateProfile(SecurityUtils.getCurrentUserId(), req),
                "Profil mis à jour avec succès"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Voir le profil public d'un utilisateur")
    public ResponseEntity<ApiResponse<PublicProfileResponse>> getPublicProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(profileService.getPublicProfile(userId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des utilisateurs", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<PublicProfileResponse>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                profileService.searchProfiles(query, PageRequest.of(page, size))));
    }
}
