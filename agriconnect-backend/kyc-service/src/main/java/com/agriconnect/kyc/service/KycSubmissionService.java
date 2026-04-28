package com.agriconnect.kyc.service;

import com.agriconnect.kyc.domain.entity.KycApplication;
import org.springframework.web.multipart.MultipartFile;

public interface KycSubmissionService {
    KycApplication submitCni(Long userId, MultipartFile cniFile);
    KycApplication validateApplication(Long applicationId, boolean isApproved);
}
