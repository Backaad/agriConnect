package com.agriconnect.labor.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.ForbiddenException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.labor.domain.entity.JobOffer;
import com.agriconnect.labor.domain.enums.JobStatus;
import com.agriconnect.labor.domain.enums.WorkType;
import com.agriconnect.labor.domain.vo.Location;
import com.agriconnect.labor.dto.request.CreateJobOfferRequest;
import com.agriconnect.labor.dto.request.NearbyJobsRequest;
import com.agriconnect.labor.dto.request.UpdateJobOfferRequest;
import com.agriconnect.labor.dto.response.JobOfferResponse;
import com.agriconnect.labor.mapper.LaborMapper;
import com.agriconnect.labor.repository.ApplicationRepository;
import com.agriconnect.labor.repository.JobOfferRepository;
import com.agriconnect.labor.service.JobOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final ApplicationRepository applicationRepository;
    private final LaborMapper laborMapper;

    @Override
    @Transactional
    public JobOfferResponse create(UUID farmerId, CreateJobOfferRequest request) {
        JobOffer offer = laborMapper.toEntity(request);
        offer.setFarmerId(farmerId);
        offer.setStatus(JobStatus.OPEN);
        offer.setExpiresAt(LocalDateTime.now().plusDays(7));

        if (request.getLatitude() != null && request.getLongitude() != null) {
            offer.setLocation(Location.builder()
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build());
        }

        offer = jobOfferRepository.save(offer);
        log.info("Offre de travail créée: id={} farmer={}", offer.getId(), farmerId);
        return enrichResponse(laborMapper.toResponse(offer));
    }

    @Override
    @Transactional(readOnly = true)
    public JobOfferResponse getById(UUID id) {
        JobOffer offer = findOfferById(id);
        JobOfferResponse response = laborMapper.toResponse(offer);
        response.setApplicationsCount((int) applicationRepository.countByJob_Id(id));
        return enrichResponse(response);
    }

    @Override
    @Transactional
    public JobOfferResponse update(UUID id, UUID farmerId, UpdateJobOfferRequest request) {
        JobOffer offer = findOfferById(id);
        checkOwnership(offer.getFarmerId(), farmerId);

        if (offer.getStatus() != JobStatus.OPEN) {
            throw new com.agriconnect.commons.exception.BusinessException(
                    "Seules les offres ouvertes peuvent être modifiées");
        }

        if (request.getDescription() != null) offer.setDescription(request.getDescription());
        if (request.getNbWorkers()    != null) offer.setNbWorkers(request.getNbWorkers());
        if (request.getSalaryFcfa()   != null) offer.setSalaryFcfa(request.getSalaryFcfa());
        if (request.getStartDate()    != null) offer.setStartDate(request.getStartDate());
        if (request.getEndDate()      != null) offer.setEndDate(request.getEndDate());
        if (request.getStartTime()    != null) offer.setStartTime(request.getStartTime());
        if (request.getEndTime()      != null) offer.setEndTime(request.getEndTime());
        if (request.getAddressText()  != null) offer.setAddressText(request.getAddressText());
        if (request.getRadiusKm()     != null) offer.setRadiusKm(request.getRadiusKm());
        if (request.getLatitude()     != null && request.getLongitude() != null) {
            offer.setLocation(Location.builder()
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build());
        }
        if (request.getToolsProvided() != null) offer.setToolsProvided(request.getToolsProvided());

        offer = jobOfferRepository.save(offer);
        log.info("Offre mise à jour: id={}", id);
        return enrichResponse(laborMapper.toResponse(offer));
    }

    @Override
    @Transactional
    public void close(UUID id, UUID farmerId) {
        JobOffer offer = findOfferById(id);
        checkOwnership(offer.getFarmerId(), farmerId);
        offer.setStatus(JobStatus.CLOSED);
        jobOfferRepository.save(offer);
        log.info("Offre fermée: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobOfferResponse> getMyOffers(UUID farmerId, Pageable pageable) {
        Page<JobOffer> page = jobOfferRepository.findByFarmerId(farmerId, pageable);
        return PageResponse.from(page.map(o -> enrichResponse(laborMapper.toResponse(o))));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobOfferResponse> getOpenOffers(String workType, Long minSalary,
                                                         String fromDate, Pageable pageable) {
        WorkType wt = workType != null ? WorkType.valueOf(workType.toUpperCase()) : null;
        LocalDate fd = fromDate != null ? LocalDate.parse(fromDate) : null;
        Page<JobOffer> page = jobOfferRepository.findOpenOffers(wt, minSalary, fd, pageable);
        return PageResponse.from(page.map(o -> enrichResponse(laborMapper.toResponse(o))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobOfferResponse> getNearbyOffers(NearbyJobsRequest request) {
        int radiusMeters = (request.getRadiusKm() != null ? request.getRadiusKm() : 20) * 1000;
        List<JobOffer> offers = jobOfferRepository.findNearby(
                request.getLatitude(), request.getLongitude(),
                radiusMeters, PageRequest.of(0, 50));
        return offers.stream()
                .map(laborMapper::toResponse)
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    private JobOffer findOfferById(UUID id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offre de travail", id.toString()));
    }

    private void checkOwnership(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new ForbiddenException("Vous n'êtes pas autorisé à modifier cette offre");
        }
    }

    private JobOfferResponse enrichResponse(JobOfferResponse r) {
        // Enrichissement avec données user-service à implémenter via Feign
        return r;
    }
}
