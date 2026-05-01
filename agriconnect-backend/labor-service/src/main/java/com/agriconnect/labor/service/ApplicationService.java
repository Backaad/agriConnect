package com.agriconnect.labor.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApplicationService {
    ApplicationResponse apply(UUID jobId, UUID workerId, ApplyRequest request);
    PageResponse<ApplicationResponse> getJobApplications(UUID jobId, UUID farmerId, String status, Pageable pageable);
    PageResponse<ApplicationResponse> getMyApplications(UUID workerId, Pageable pageable);
    ApplicationResponse accept(UUID applicationId, UUID farmerId);
    ApplicationResponse reject(UUID applicationId, UUID farmerId);
    void withdraw(UUID applicationId, UUID workerId);
}
