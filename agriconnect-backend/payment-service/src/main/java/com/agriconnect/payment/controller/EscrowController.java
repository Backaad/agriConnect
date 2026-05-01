package com.agriconnect.payment.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.dto.request.EscrowReleaseRequest;
import com.agriconnect.payment.dto.response.EscrowResponse;
import com.agriconnect.payment.service.EscrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments/escrow")
@RequiredArgsConstructor
@Tag(name = "Escrow", description = "Gestion des fonds en séquestre (usage interne services)")
public class EscrowController {

    private final EscrowService escrowService;

    @PostMapping("/lock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[INTERNE] Verrouiller un escrow",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EscrowResponse>> lock(
            @Valid @RequestBody EscrowLockRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                escrowService.lock(request), "Escrow verrouillé"));
    }

    @PostMapping("/release")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[INTERNE] Libérer un escrow",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EscrowResponse>> release(
            @Valid @RequestBody EscrowReleaseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                escrowService.release(request), "Escrow libéré — paiement effectué"));
    }

    @PostMapping("/refund/{referenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Rembourser un escrow (litige résolu)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EscrowResponse>> refund(
            @PathVariable UUID referenceId,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(
                escrowService.refund(referenceId, reason), "Escrow remboursé"));
    }

    @GetMapping("/{referenceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Statut escrow pour une référence",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<EscrowResponse>> getEscrow(
            @PathVariable UUID referenceId) {
        return ResponseEntity.ok(ApiResponse.success(escrowService.getByReference(referenceId)));
    }
}
