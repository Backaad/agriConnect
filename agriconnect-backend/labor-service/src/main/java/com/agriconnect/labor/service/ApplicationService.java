package com.agriconnect.labor.service;

import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyForMission(ApplyRequest request, String workerId);
    ApplicationResponse acceptApplication(Long applicationId, String employerId);
    ApplicationResponse rejectApplication(Long applicationId, String employerId);
    List<ApplicationResponse> getWorkerApplications(String workerId);
    List<ApplicationResponse> getMissionApplications(Long missionId, String employerId);
}
