package com.agriconnect.kyc.service.impl;

import com.agriconnect.kyc.domain.entity.KycApplication;
import com.agriconnect.kyc.domain.enums.KycStatus;
import com.agriconnect.kyc.repository.KycApplicationRepository;
import com.agriconnect.kyc.service.KycSubmissionService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KycSubmissionServiceImpl implements KycSubmissionService {

    private final KycApplicationRepository kycApplicationRepository;
    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket:dummy-bucket}")
    private String bucketName;

    @Override
    public KycApplication submitCni(Long userId, MultipartFile cniFile) {
        String fileName = "cni/" + userId + "/" + UUID.randomUUID() + "-" + cniFile.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(cniFile.getSize());
            metadata.setContentType(cniFile.getContentType());

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, cniFile.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload CNI to S3", e);
        }

        String s3Url = s3Client.getUrl(bucketName, fileName).toString();

        KycApplication application = kycApplicationRepository.findByUserId(userId)
                .orElse(KycApplication.builder().userId(userId).build());

        application.setCniS3Url(s3Url);
        application.setStatus(KycStatus.PENDING);

        return kycApplicationRepository.save(application);
    }

    @Override
    public KycApplication validateApplication(Long applicationId, boolean isApproved) {
        KycApplication application = kycApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("KYC Application not found"));

        application.setStatus(isApproved ? KycStatus.APPROVED : KycStatus.REJECTED);
        return kycApplicationRepository.save(application);
    }
}
