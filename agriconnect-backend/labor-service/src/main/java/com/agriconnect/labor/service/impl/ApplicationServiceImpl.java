package com.agriconnect.labor.service.impl;

import com.agriconnect.labor.domain.entity.Application;
import com.agriconnect.labor.domain.entity.Contract;
import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.ApplicationStatus;
import com.agriconnect.labor.domain.enums.ContractStatus;
import com.agriconnect.labor.domain.enums.MissionStatus;
import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import com.agriconnect.labor.repository.ApplicationRepository;
import com.agriconnect.labor.repository.ContractRepository;
import com.agriconnect.labor.repository.MissionRepository;
import com.agriconnect.labor.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final MissionRepository missionRepository;
    private final ContractRepository contractRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  MissionRepository missionRepository,
                                  ContractRepository contractRepository) {
        this.applicationRepository = applicationRepository;
        this.missionRepository = missionRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public ApplicationResponse applyForMission(ApplyRequest request, String workerId) {
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (mission.getStatus() != MissionStatus.OPEN) {
            throw new RuntimeException("Mission is not open for applications");
        }

        Application application = Application.builder()
                .mission(mission)
                .workerId(workerId)
                .status(ApplicationStatus.PENDING)
                .build();

        return mapToResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public ApplicationResponse acceptApplication(Long applicationId, String employerId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getMission().getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatus(ApplicationStatus.ACCEPTED);
        application = applicationRepository.save(application);

        Mission mission = application.getMission();
        mission.setStatus(MissionStatus.IN_PROGRESS);
        missionRepository.save(mission);

        // Auto-generate pending contract
        Contract contract = Contract.builder()
                .application(application)
                .status(ContractStatus.PENDING_SIGNATURE)
                .build();
        contractRepository.save(contract);

        return mapToResponse(application);
    }

    @Override
    public ApplicationResponse rejectApplication(Long applicationId, String employerId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getMission().getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        return mapToResponse(applicationRepository.save(application));
    }

    @Override
    public List<ApplicationResponse> getWorkerApplications(String workerId) {
        return applicationRepository.findByWorkerId(workerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationResponse> getMissionApplications(Long missionId, String employerId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (!mission.getEmployerId().equals(employerId)) {
            throw new RuntimeException("Unauthorized");
        }

        return applicationRepository.findByMissionId(missionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .missionId(application.getMission().getId())
                .workerId(application.getWorkerId())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
