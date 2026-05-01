package com.agriconnect.auth.controller;

import com.agriconnect.auth.dto.request.OtpSendRequest;
import com.agriconnect.auth.dto.request.OtpVerifyRequest;
import com.agriconnect.auth.dto.response.OtpResponse;
import com.agriconnect.auth.service.OtpService;
import com.agriconnect.commons.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/otp")
@RequiredArgsConstructor
@Tag(name = "OTP", description = "Envoi et vérification des codes OTP")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    @Operation(summary = "Envoyer un code OTP par SMS")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        OtpResponse response = otpService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Code OTP envoyé"));
    }

    @PostMapping("/verify")
    @Operation(summary = "Vérifier un code OTP")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = otpService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verified, "Numéro de téléphone vérifié avec succès"));
    }
}
