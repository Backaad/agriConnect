package com.agriconnect.labor.controller;

import com.agriconnect.labor.dto.request.CompleteMissionRequest;
import com.agriconnect.labor.dto.request.MissionRequest;
import com.agriconnect.labor.dto.response.MissionResponse;
import com.agriconnect.labor.security.JwtTokenProvider;
import com.agriconnect.labor.service.MissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labor/missions")
public class MissionController {

    private final MissionService missionService;
    private final JwtTokenProvider tokenProvider;

    public MissionController(MissionService missionService, JwtTokenProvider tokenProvider) {
        this.missionService = missionService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping
    public ResponseEntity<MissionResponse> createMission(@RequestBody MissionRequest request,
                                                         @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(missionService.createMission(request, employerId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<MissionResponse>> getNearbyMissions(@RequestParam double lat,
                                                                   @RequestParam double lon,
                                                                   @RequestParam double radius) {
        return ResponseEntity.ok(missionService.findMissionsWithinRadius(lat, lon, radius));
    }

    @GetMapping("/employer")
    public ResponseEntity<List<MissionResponse>> getEmployerMissions(@RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(missionService.getEmployerMissions(employerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionResponse> getMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MissionResponse> updateMission(@PathVariable Long id,
                                                         @RequestBody MissionRequest request,
                                                         @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(missionService.updateMission(id, request, employerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id,
                                              @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        missionService.deleteMission(id, employerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/complete")
    public ResponseEntity<MissionResponse> completeMission(@RequestBody CompleteMissionRequest request,
                                                           @RequestHeader("Authorization") String token) {
        String employerId = getUserId(token);
        return ResponseEntity.ok(missionService.completeMission(request.getMissionId(), employerId));
    }

    private String getUserId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return tokenProvider.getUserIdFromJWT(authHeader.substring(7));
        }
        throw new RuntimeException("Invalid token");
    }
}
