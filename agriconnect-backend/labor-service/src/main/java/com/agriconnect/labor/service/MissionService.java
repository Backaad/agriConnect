package com.agriconnect.labor.service;

import com.agriconnect.labor.dto.request.MissionRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import java.util.List;

public interface MissionService {
    MissionResponse createMission(MissionRequest request, String employerId);
    List<MissionResponse> findMissionsWithinRadius(double lat, double lon, double radiusKm);
    List<MissionResponse> getEmployerMissions(String employerId);
    MissionResponse getMissionById(Long id);
    MissionResponse updateMission(Long id, MissionRequest request, String employerId);
    void deleteMission(Long id, String employerId);
    MissionResponse completeMission(Long id, String employerId);
}
