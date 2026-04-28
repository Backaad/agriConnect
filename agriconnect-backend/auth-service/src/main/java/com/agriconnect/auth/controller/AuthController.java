package com.agriconnect.auth.controller;

import com.agriconnect.auth.domain.entity.User;
import com.agriconnect.auth.domain.enums.UserStatus;
import com.agriconnect.auth.dto.request.RegisterRequest;
import com.agriconnect.auth.repository.UserRepository;
import com.agriconnect.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElse(User.builder()
                        .phoneNumber(request.getPhoneNumber())
                        .status(UserStatus.PENDING_OTP)
                        .build());
        userRepository.save(user);

        otpService.sendOtp(user.getPhoneNumber());

        return ResponseEntity.ok("OTP sent successfully to " + user.getPhoneNumber());
    }
}
