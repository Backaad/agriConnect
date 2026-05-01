package com.agriconnect.payment.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.payment.dto.request.WithdrawRequest;
import com.agriconnect.payment.dto.response.WithdrawalResponse;
import com.agriconnect.payment.security.SecurityUtils;
import com.agriconnect.payment.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments/withdrawals")
@RequiredArgsConstructor
@Tag(name = "Withdrawals", description = "Retraits vers Mobile Money")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Demander un retrait vers Mobile Money",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<WithdrawalResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                withdrawalService.requestWithdrawal(SecurityUtils.getCurrentUserId(), request),
                "Retrait effectué avec succès"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Historique de mes retraits",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<WithdrawalResponse>>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                withdrawalService.getHistory(
                        SecurityUtils.getCurrentUserId(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }
}
