package com.agriconnect.labor.service;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.CompleteMissionRequest;
import com.agriconnect.labor.dto.request.DisputeRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MissionService {
    MissionResponse getById(UUID id, UUID requesterId);
    MissionResponse startMission(UUID missionId, UUID farmerId);
    MissionResponse validateCompletion(UUID missionId, UUID requesterId, CompleteMissionRequest request);
    MissionResponse openDispute(UUID missionId, UUID requesterId, DisputeRequest request);
    PageResponse<MissionResponse> getMyMissions(UUID userId, String role, String status, Pageable pageable);
}
