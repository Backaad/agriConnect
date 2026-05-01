package com.agriconnect.labor.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.ForbiddenException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.MissionStatus;
import com.agriconnect.labor.dto.request.CompleteMissionRequest;
import com.agriconnect.labor.dto.request.DisputeRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import com.agriconnect.labor.event.model.MissionCompletedEvent;
import com.agriconnect.labor.event.model.MissionDisputedEvent;
import com.agriconnect.labor.event.publisher.LaborEventPublisher;
import com.agriconnect.labor.mapper.LaborMapper;
import com.agriconnect.labor.repository.MissionRepository;
import com.agriconnect.labor.service.MissionService;
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
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final LaborMapper laborMapper;
    private final LaborEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public MissionResponse getById(UUID id, UUID requesterId) {
        Mission mission = findMissionById(id);
        checkAccess(mission, requesterId);
        return laborMapper.toMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse startMission(UUID missionId, UUID farmerId) {
        Mission mission = findMissionById(missionId);
        if (!mission.getFarmerId().equals(farmerId)) {
            throw new ForbiddenException("Seul l'agriculteur peut démarrer la mission");
        }
        if (mission.getStatus() != MissionStatus.SCHEDULED) {
            throw new BusinessException("La mission ne peut pas être démarrée dans son état actuel");
        }
        mission.setStatus(MissionStatus.IN_PROGRESS);
        mission.setStartedAt(LocalDateTime.now());
        mission.setUpdatedAt(LocalDateTime.now());
        mission = missionRepository.save(mission);
        log.info("Mission démarrée: id={}", missionId);
        return laborMapper.toMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse validateCompletion(UUID missionId, UUID requesterId, CompleteMissionRequest request) {
        Mission mission = findMissionById(missionId);
        checkAccess(mission, requesterId);

        if (mission.getStatus() != MissionStatus.IN_PROGRESS
                && mission.getStatus() != MissionStatus.COMPLETED) {
            throw new BusinessException("La mission ne peut pas être validée dans son état actuel");
        }

        boolean isFarmer = mission.getFarmerId().equals(requesterId);
        boolean isWorker = mission.getWorkerId().equals(requesterId);
        LocalDateTime now = LocalDateTime.now();

        if (isFarmer) {
            if (mission.getFarmerValidatedAt() != null) throw new BusinessException("Vous avez déjà validé cette mission");
            mission.setFarmerValidatedAt(now);
            mission.setFarmerRating(request.getRating());
            mission.setFarmerReview(request.getReview());
        }
        if (isWorker) {
            if (mission.getWorkerValidatedAt() != null) throw new BusinessException("Vous avez déjà validé cette mission");
            mission.setWorkerValidatedAt(now);
            mission.setWorkerRating(request.getRating());
            mission.setWorkerReview(request.getReview());
        }

        mission.setStatus(MissionStatus.COMPLETED);
        mission.setUpdatedAt(now);

        if (mission.isFullyValidated()) {
            mission.setStatus(MissionStatus.VALIDATED);
            mission.setCompletedAt(now);

            // Publier événement → libérer l'escrow
            eventPublisher.publishMissionCompleted(MissionCompletedEvent.builder()
                    .missionId(mission.getId())
                    .contractId(mission.getContract().getId())
                    .farmerId(mission.getFarmerId())
                    .workerId(mission.getWorkerId())
                    .amountFcfa(mission.getContract().getAmountFcfa())
                    .build());

            log.info("Mission validée par les deux parties et paiement libéré: id={}", missionId);
        }

        mission = missionRepository.save(mission);
        return laborMapper.toMissionResponse(mission);
    }

    @Override
    @Transactional
    public MissionResponse openDispute(UUID missionId, UUID requesterId, DisputeRequest request) {
        Mission mission = findMissionById(missionId);
        checkAccess(mission, requesterId);

        if (mission.getStatus() != MissionStatus.IN_PROGRESS
                && mission.getStatus() != MissionStatus.COMPLETED) {
            throw new BusinessException("Un litige ne peut être ouvert que sur une mission en cours ou terminée");
        }

        mission.setStatus(MissionStatus.DISPUTED);
        mission.setDisputeReason(request.getReason());
        mission.setUpdatedAt(LocalDateTime.now());
        mission = missionRepository.save(mission);

        eventPublisher.publishMissionDisputed(MissionDisputedEvent.builder()
                .missionId(mission.getId())
                .contractId(mission.getContract().getId())
                .farmerId(mission.getFarmerId())
                .workerId(mission.getWorkerId())
                .reason(request.getReason())
                .reporterId(requesterId)
                .build());

        log.info("Litige ouvert sur mission: id={} par userId={}", missionId, requesterId);
        return laborMapper.toMissionResponse(mission);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MissionResponse> getMyMissions(UUID userId, String role, String status, Pageable pageable) {
        Page<Mission> page;
        MissionStatus ms = status != null ? MissionStatus.valueOf(status.toUpperCase()) : null;

        if ("FARMER".equalsIgnoreCase(role)) {
            page = ms != null
                    ? missionRepository.findByFarmerIdAndStatus(userId, ms, pageable)
                    : missionRepository.findByFarmerId(userId, pageable);
        } else {
            page = ms != null
                    ? missionRepository.findByWorkerIdAndStatus(userId, ms, pageable)
                    : missionRepository.findByWorkerId(userId, pageable);
        }
        return PageResponse.from(page.map(laborMapper::toMissionResponse));
    }

    private Mission findMissionById(UUID id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mission", id.toString()));
    }

    private void checkAccess(Mission mission, UUID userId) {
        if (!mission.getFarmerId().equals(userId) && !mission.getWorkerId().equals(userId)) {
            throw new ForbiddenException("Accès à la mission refusé");
        }
    }
}
