package com.agriconnect.labor.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.CreateJobOfferRequest;
import com.agriconnect.labor.dto.request.NearbyJobsRequest;
import com.agriconnect.labor.dto.request.UpdateJobOfferRequest;
import com.agriconnect.labor.dto.response.JobOfferResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface JobOfferService {
    JobOfferResponse create(UUID farmerId, CreateJobOfferRequest request);
    JobOfferResponse getById(UUID id);
    JobOfferResponse update(UUID id, UUID farmerId, UpdateJobOfferRequest request);
    void close(UUID id, UUID farmerId);
    PageResponse<JobOfferResponse> getMyOffers(UUID farmerId, Pageable pageable);
    PageResponse<JobOfferResponse> getOpenOffers(String workType, Long minSalary, String fromDate, Pageable pageable);
    List<JobOfferResponse> getNearbyOffers(NearbyJobsRequest request);
}
