package com.agriconnect.labor.service.impl;

import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.MissionStatus;
import com.agriconnect.labor.dto.request.MissionRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import com.agriconnect.labor.repository.MissionRepository;
import com.agriconnect.labor.service.MissionService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionServiceImpl implements MissionService {

    private final MissionRepository missionRepository;
    private final GeometryFactory geometryFactory;

    public MissionServiceImpl(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    @Override
    public MissionResponse createMission(MissionRequest request, String employerId) {
        Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));

        Mission mission = Mission.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .salary(request.getSalary())
                .employerId(employerId)
                .location(location)
                .status(MissionStatus.OPEN)
                .build();

        mission = missionRepository.save(mission);
        return mapToResponse(mission);
    }

    @Override
    public List<MissionResponse> findMissionsWithinRadius(double lat, double lon, double radiusKm) {
        Point workerLocation = geometryFactory.createPoint(new Coordinate(lon, lat));
        // Rough conversion: 1 degree is approx 111km
        double radiusInDegrees = radiusKm / 111.0;

        return missionRepository.findMissionsWithinRadius(workerLocation, radiusInDegrees, MissionStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MissionResponse> getEmployerMissions(String employerId) {
        return missionRepository.findByEmployerId(employerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MissionResponse getMissionById(Long id) {
        return mapToResponse(missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found")));
    }

    @Override
    public MissionResponse updateMission(Long id, MissionRequest request, String employerId) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (!mission.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        mission.setTitle(request.getTitle());
        mission.setDescription(request.getDescription());
        mission.setSalary(request.getSalary());

        Point location = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        mission.setLocation(location);

        return mapToResponse(missionRepository.save(mission));
    }

    @Override
    public void deleteMission(Long id, String employerId) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (!mission.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        missionRepository.delete(mission);
    }

    @Override
    public MissionResponse completeMission(Long id, String employerId) {
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (!mission.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        mission.setStatus(MissionStatus.COMPLETED);
        return mapToResponse(missionRepository.save(mission));
    }

    private MissionResponse mapToResponse(Mission mission) {
        return MissionResponse.builder()
                .id(mission.getId())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .salary(mission.getSalary())
                .employerId(mission.getEmployerId())
                .latitude(mission.getLocation().getY())
                .longitude(mission.getLocation().getX())
                .status(mission.getStatus())
                .createdAt(mission.getCreatedAt())
                .build();
    }
}
