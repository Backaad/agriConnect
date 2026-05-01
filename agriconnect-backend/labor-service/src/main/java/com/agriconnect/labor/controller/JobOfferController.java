package com.agriconnect.labor.controller;

import com.agriconnect.commons.dto.ApiResponse;
import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.labor.dto.request.CreateJobOfferRequest;
import com.agriconnect.labor.dto.request.NearbyJobsRequest;
import com.agriconnect.labor.dto.request.UpdateJobOfferRequest;
import com.agriconnect.labor.dto.response.JobOfferResponse;
import com.agriconnect.labor.security.SecurityUtils;
import com.agriconnect.labor.service.JobOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Offers", description = "Gestion des offres de main-d'oeuvre agricole")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Publier une offre de travail", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<JobOfferResponse>> create(@Valid @RequestBody CreateJobOfferRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        jobOfferService.create(SecurityUtils.getCurrentUserId(), req),
                        "Offre publiée avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une offre")
    public ResponseEntity<ApiResponse<JobOfferResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(jobOfferService.getById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Modifier une offre", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<JobOfferResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateJobOfferRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                jobOfferService.update(id, SecurityUtils.getCurrentUserId(), req),
                "Offre mise à jour"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Fermer une offre", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
        jobOfferService.close(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(null, "Offre fermée"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FARMER')")
    @Operation(summary = "Mes offres publiées", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PageResponse<JobOfferResponse>>> getMyOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                jobOfferService.getMyOffers(SecurityUtils.getCurrentUserId(),
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping
    @Operation(summary = "Liste des offres ouvertes (avec filtres)")
    public ResponseEntity<ApiResponse<PageResponse<JobOfferResponse>>> getOpenOffers(
            @RequestParam(required = false) String workType,
            @RequestParam(required = false) Long minSalary,
            @RequestParam(required = false) String fromDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                jobOfferService.getOpenOffers(workType, minSalary, fromDate,
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Offres proches de ma position (géolocalisation)")
    public ResponseEntity<ApiResponse<List<JobOfferResponse>>> getNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "20") int radiusKm,
            @RequestParam(required = false) String workType) {
        NearbyJobsRequest req = new NearbyJobsRequest();
        req.setLatitude(lat);
        req.setLongitude(lng);
        req.setRadiusKm(radiusKm);
        return ResponseEntity.ok(ApiResponse.success(jobOfferService.getNearbyOffers(req)));
    }
}
