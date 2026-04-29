package com.agriconnect.kyc.controller;

import com.agriconnect.kyc.domain.entity.KycApplication;
import com.agriconnect.kyc.service.KycSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc/submit")
@RequiredArgsConstructor
public class KycSubmissionController {

    private final KycSubmissionService kycSubmissionService;

    @PostMapping("/cni/{userId}")
    public ResponseEntity<KycApplication> submitCni(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile cniFile) {
        
        KycApplication application = kycSubmissionService.submitCni(userId, cniFile);
        return ResponseEntity.ok(application);
    }
}
