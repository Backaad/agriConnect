package com.agriconnect.labor.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;
import com.agriconnect.labor.security.SecurityUtils;
import com.agriconnect.labor.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Génération et signature des contrats de mission")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/application/{applicationId}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Générer un contrat après acceptation de candidature",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ContractResponse>> generate(@PathVariable UUID applicationId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        contractService.generateContract(applicationId, SecurityUtils.getCurrentUserId()),
                        "Contrat généré avec succès"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Détail d'un contrat", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ContractResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                contractService.getById(id, SecurityUtils.getCurrentUserId())));
    }

    @PostMapping("/{id}/sign")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Signer un contrat numériquement", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ContractResponse>> sign(
            @PathVariable UUID id,
            @Valid @RequestBody SignContractRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                contractService.sign(id, SecurityUtils.getCurrentUserId(), req),
                "Contrat signé avec succès"));
    }
}
