package com.agriconnect.auth.controller;

import com.agriconnect.auth.domain.entity.User;
import com.agriconnect.auth.domain.enums.UserStatus;
import com.agriconnect.auth.dto.request.OtpVerifyRequest;
import com.agriconnect.auth.repository.UserRepository;
import com.agriconnect.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final UserRepository userRepository;

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        boolean isValid = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        
        if (isValid) {
            userRepository.findByPhoneNumber(request.getPhoneNumber()).ifPresent(user -> {
                user.setStatus(UserStatus.ACTIVE);
                userRepository.save(user);
            });
            return ResponseEntity.ok("OTP verified successfully. User is now active.");
        }
        
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }
}
