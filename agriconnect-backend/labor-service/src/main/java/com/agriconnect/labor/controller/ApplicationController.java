package com.agriconnect.labor.controller;

import com.agriconnect.labor.dto.request.ApplyRequest;
import com.agriconnect.labor.dto.response.ApplicationResponse;
import com.agriconnect.labor.security.JwtTokenProvider;
import com.agriconnect.labor.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labor/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtTokenProvider tokenProvider;

    public ApplicationController(ApplicationService applicationService, JwtTokenProvider tokenProvider) {
        this.applicationService = applicationService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/apply")
    public ResponseEntity<ApplicationResponse> apply(@RequestBody ApplyRequest request,
                                                     @RequestHeader("Authorization") String token) {
        String workerId = getUserId(token);
        return ResponseEntity.ok(applicationService.applyForMission(request, workerId));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApplicationResponse> acceptApplication(@PathVariable Long id,
                                                                 @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(applicationService.acceptApplication(id, employerId));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> rejectApplication(@PathVariable Long id,
                                                                 @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(applicationService.rejectApplication(id, employerId));
    }

    @GetMapping("/worker")
    public ResponseEntity<List<ApplicationResponse>> getWorkerApplications(@RequestHeader("Authorization") String token) {
        String workerId = getUserId(token);
        return ResponseEntity.ok(applicationService.getWorkerApplications(workerId));
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<ApplicationResponse>> getMissionApplications(@PathVariable Long missionId,
                                                                            @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(applicationService.getMissionApplications(missionId, employerId));
    }

    private String getUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromJWT(authHeader.substring(7));
        }
        throw new RuntimeException("Invalid token");
    }
}
