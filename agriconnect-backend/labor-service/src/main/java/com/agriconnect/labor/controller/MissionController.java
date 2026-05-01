package com.agriconnect.labor.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.CompleteMissionRequest;
import com.agriconnect.labor.dto.request.DisputeRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import com.agriconnect.labor.security.SecurityUtils;
import com.agriconnect.labor.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Suivi et validation des missions agricoles")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Détail d'une mission", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<MissionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                missionService.getById(id, SecurityUtils.getCurrentUserId())));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Démarrer une mission", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<MissionResponse>> start(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                missionService.startMission(id, SecurityUtils.getCurrentUserId()),
                "Mission démarrée"));
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Valider la fin de mission (libère le paiement si les deux valident)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<MissionResponse>> validate(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteMissionRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                missionService.validateCompletion(id, SecurityUtils.getCurrentUserId(), req),
                "Validation enregistrée"));
    }

    @PostMapping("/{id}/dispute")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Ouvrir un litige sur une mission", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<MissionResponse>> dispute(
            @PathVariable UUID id,
            @Valid @RequestBody DisputeRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                missionService.openDispute(id, SecurityUtils.getCurrentUserId(), req),
                "Litige ouvert — notre équipe vous contactera sous 24h"));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mes missions (en tant que farmer ou worker)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<MissionResponse>>> getMyMissions(
            @RequestParam(defaultValue = "WORKER") String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                missionService.getMyMissions(SecurityUtils.getCurrentUserId(), role, status,
                        PageRequest.of(page, size))));
    }
}
