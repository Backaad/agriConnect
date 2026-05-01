package com.agriconnect.user.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.user.domain.enums.Role;
import com.agriconnect.user.dto.request.AddRoleRequest;
import com.agriconnect.user.security.SecurityUtils;
import com.agriconnect.user.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users/me/roles")
@RequiredArgsConstructor
@Tag(name = "User Roles", description = "Gestion des rôles utilisateur")
public class UserRoleController {

    private final UserRoleService roleService;

    @GetMapping
    @Operation(summary = "Lister mes rôles", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Set<Role>>> getMyRoles() {
        return ResponseEntity.ok(ApiResponse.success(roleService.getUserRoles(SecurityUtils.getCurrentUserId())));
    }

    @PostMapping
    @Operation(summary = "Ajouter un rôle", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> addRole(@Valid @RequestBody AddRoleRequest request) {
        Role role = Role.valueOf(request.getRole().toUpperCase());
        roleService.addRole(SecurityUtils.getCurrentUserId(), role);
        return ResponseEntity.ok(ApiResponse.success(null, "Rôle ajouté: " + role.name()));
    }

    @DeleteMapping("/{role}")
    @Operation(summary = "Retirer un rôle", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable String role) {
        roleService.removeRole(SecurityUtils.getCurrentUserId(), Role.valueOf(role.toUpperCase()));
        return ResponseEntity.ok(ApiResponse.success(null, "Rôle retiré"));
    }
}
