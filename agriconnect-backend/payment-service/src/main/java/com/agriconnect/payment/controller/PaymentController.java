package com.agriconnect.payment.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.payment.dto.request.TopUpRequest;
import com.agriconnect.payment.dto.response.PaymentInitResponse;
import com.agriconnect.payment.dto.response.TransactionResponse;
import com.agriconnect.payment.security.SecurityUtils;
import com.agriconnect.payment.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Rechargement et historique des transactions")
public class PaymentController {

    private final TransactionService transactionService;

    @PostMapping("/topup")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Recharger le wallet via Mobile Money",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PaymentInitResponse>> topUp(
            @Valid @RequestBody TopUpRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(
                        transactionService.topUp(SecurityUtils.getCurrentUserId(), request),
                        "Paiement initié — confirmez sur votre téléphone"));
    }

    @GetMapping("/transactions/{reference}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Statut d'une transaction",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @PathVariable String reference) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getByReference(reference, SecurityUtils.getCurrentUserId())));
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Historique complet des transactions",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getHistory(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getHistory(
                        SecurityUtils.getCurrentUserId(), type,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
}
