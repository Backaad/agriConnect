package com.agriconnect.user.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.user.security.SecurityUtils;
import com.agriconnect.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users/me/avatar")
@RequiredArgsConstructor
@Tag(name = "User Avatar", description = "Upload et gestion des photos de profil")
public class UserAvatarController {

    private final UserProfileService profileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader/mettre à jour mon avatar", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String avatarUrl = profileService.updateAvatar(SecurityUtils.getCurrentUserId(), file);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl, "Avatar mis à jour avec succès"));
    }
}
