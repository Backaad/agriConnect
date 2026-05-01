package com.agriconnect.labor.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import com.agriconnect.labor.security.SecurityUtils;
import com.agriconnect.labor.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Gestion des candidatures aux offres de travail")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/job/{jobId}")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Candidater à une offre", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @PathVariable UUID jobId,
            @Valid @RequestBody ApplyRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        applicationService.apply(jobId, SecurityUtils.getCurrentUserId(), req),
                        "Candidature envoyée avec succès"));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Candidatures reçues pour une offre", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getJobApplications(
            @PathVariable UUID jobId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getJobApplications(jobId, SecurityUtils.getCurrentUserId(),
                        status, PageRequest.of(page, size))));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Mes candidatures", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getMyApplications(SecurityUtils.getCurrentUserId(),
                        PageRequest.of(page, size))));
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Accepter une candidature", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ApplicationResponse>> accept(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.accept(id, SecurityUtils.getCurrentUserId()),
                "Candidature acceptée"));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Refuser une candidature", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ApplicationResponse>> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.reject(id, SecurityUtils.getCurrentUserId()),
                "Candidature refusée"));
    }

    @DeleteMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('WORKER')")
    @Operation(summary = "Retirer ma candidature", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable UUID id) {
        applicationService.withdraw(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Candidature retirée"));
    }
}
