package com.agriconnect.kyc.controller;

import com.agriconnect.kyc.domain.entity.KycApplication;
import com.agriconnect.kyc.service.KycSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/kyc")
@RequiredArgsConstructor
public class KycAdminController {

    private final KycSubmissionService kycSubmissionService;

    @PutMapping("/{applicationId}/validate")
    public ResponseEntity<KycApplication> validateKycApplication(
            @PathVariable Long applicationId,
            @RequestParam boolean isApproved) {
        
        KycApplication updatedApplication = kycSubmissionService.validateApplication(applicationId, isApproved);
        return ResponseEntity.ok(updatedApplication);
    }
}
