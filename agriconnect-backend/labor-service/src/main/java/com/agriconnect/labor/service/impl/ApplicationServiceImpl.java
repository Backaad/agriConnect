package com.agriconnect.labor.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.ConflictException;
import com.agriconnect.commons.exception.ForbiddenException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.labor.domain.entity.Application;
import com.agriconnect.labor.domain.entity.JobOffer;
import com.agriconnect.labor.domain.enums.ApplicationStatus;
import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import com.agriconnect.labor.event.model.ApplicationAcceptedEvent;
import com.agriconnect.labor.event.publisher.LaborEventPublisher;
import com.agriconnect.labor.mapper.LaborMapper;
import com.agriconnect.labor.repository.ApplicationRepository;
import com.agriconnect.labor.repository.JobOfferRepository;
import com.agriconnect.labor.service.ApplicationService;
import com.agriconnect.labor.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobOfferRepository jobOfferRepository;
    private final LaborMapper laborMapper;
    private final LaborEventPublisher eventPublisher;

    @Override
    @Transactional
    public ApplicationResponse apply(UUID jobId, UUID workerId, ApplyRequest request) {
        JobOffer job = jobOfferRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Offre", jobId.toString()));

        if (!job.isOpen()) {
            throw new BusinessException("Cette offre n'est plus disponible", "JOB_CLOSED");
        }
        if (job.getFarmerId().equals(workerId)) {
            throw new BusinessException("Vous ne pouvez pas candidater à votre propre offre");
        }
        if (applicationRepository.existsByJob_IdAndWorkerId(jobId, workerId)) {
            throw new ConflictException("Vous avez déjà candidaté à cette offre");
        }

        Application application = Application.builder()
                .job(job)
                .workerId(workerId)
                .coverNote(request.getCoverNote())
                .status(ApplicationStatus.PENDING)
                .build();

        application = applicationRepository.save(application);
        log.info("Candidature créée: id={} job={} worker={}", application.getId(), jobId, workerId);
        return laborMapper.toApplicationResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getJobApplications(UUID jobId, UUID farmerId,
                                                                  String status, Pageable pageable) {
        JobOffer job = jobOfferRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Offre", jobId.toString()));
        if (!job.getFarmerId().equals(farmerId)) {
            throw new ForbiddenException("Accès refusé aux candidatures de cette offre");
        }
        Page<Application> page = status != null
                ? applicationRepository.findByJob_IdAndStatus(jobId, ApplicationStatus.valueOf(status.toUpperCase()), pageable)
                : applicationRepository.findByJob_Id(jobId, pageable);
        return PageResponse.from(page.map(laborMapper::toApplicationResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getMyApplications(UUID workerId, Pageable pageable) {
        Page<Application> page = applicationRepository.findByWorkerId(workerId, pageable);
        return PageResponse.from(page.map(laborMapper::toApplicationResponse));
    }

    @Override
    @Transactional
    public ApplicationResponse accept(UUID applicationId, UUID farmerId) {
        Application application = findApplicationById(applicationId);
        checkJobOwnership(application.getJob().getFarmerId(), farmerId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException("Cette candidature ne peut plus être acceptée");
        }

        application.setStatus(ApplicationStatus.ACCEPTED);
        application.setReviewedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        eventPublisher.publishApplicationAccepted(ApplicationAcceptedEvent.builder()
                .applicationId(application.getId())
                .jobId(application.getJob().getId())
                .farmerId(farmerId)
                .workerId(application.getWorkerId())
                .build());

        log.info("Candidature acceptée: id={}", applicationId);
        return laborMapper.toApplicationResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse reject(UUID applicationId, UUID farmerId) {
        Application application = findApplicationById(applicationId);
        checkJobOwnership(application.getJob().getFarmerId(), farmerId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessException("Cette candidature ne peut plus être refusée");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedAt(LocalDateTime.now());
        application = applicationRepository.save(application);

        log.info("Candidature refusée: id={}", applicationId);
        return laborMapper.toApplicationResponse(application);
    }

    @Override
    @Transactional
    public void withdraw(UUID applicationId, UUID workerId) {
        Application application = findApplicationById(applicationId);
        if (!application.getWorkerId().equals(workerId)) {
            throw new ForbiddenException("Vous ne pouvez retirer que vos propres candidatures");
        }
        if (application.getStatus() == ApplicationStatus.ACCEPTED) {
            throw new BusinessException("Vous ne pouvez pas retirer une candidature acceptée");
        }
        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
        log.info("Candidature retirée: id={}", applicationId);
    }

    private Application findApplicationById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Candidature", id.toString()));
    }

    private void checkJobOwnership(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new ForbiddenException("Accès refusé");
        }
    }
}
