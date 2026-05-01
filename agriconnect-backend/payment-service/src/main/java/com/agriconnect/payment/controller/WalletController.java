package com.agriconnect.payment.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.payment.dto.response.WalletResponse;
import com.agriconnect.payment.security.SecurityUtils;
import com.agriconnect.payment.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Consultation du portefeuille AgriConnect")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mon portefeuille (solde + solde gelé)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<WalletResponse>> getMyWallet() {
        return ResponseEntity.ok(ApiResponse.success(
                walletService.getOrCreateWallet(SecurityUtils.getCurrentUserId())));
    }
}
