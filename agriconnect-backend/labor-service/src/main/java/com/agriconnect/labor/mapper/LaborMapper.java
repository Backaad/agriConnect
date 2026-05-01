package com.agriconnect.labor.mapper;

import com.agriconnect.labor.domain.entity.*;
import com.agriconnect.labor.dto.request.CreateJobOfferRequest;
import com.agriconnect.labor.dto.response.*;
import com.agriconnect.labor.domain.vo.Location;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LaborMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farmerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "toolsProvided", source = "toolsProvided")
    @Mapping(target = "location", expression = "java(mapLocation(request))")
    JobOffer toEntity(CreateJobOfferRequest request);

    @Mapping(target = "latitude",  expression = "java(offer.getLocation() != null ? offer.getLocation().getLatitude()  : null)")
    @Mapping(target = "longitude", expression = "java(offer.getLocation() != null ? offer.getLocation().getLongitude() : null)")
    @Mapping(target = "totalAmountFcfa", expression = "java(offer.getTotalAmount())")
    @Mapping(target = "workTypeLabel", expression = "java(offer.getWorkType().name())")
    @Mapping(target = "farmerName", ignore = true)
    @Mapping(target = "farmerAvatarUrl", ignore = true)
    @Mapping(target = "farmerKycVerified", ignore = true)
    @Mapping(target = "applicationsCount", ignore = true)
    @Mapping(target = "distanceKm", ignore = true)
    JobOfferResponse toResponse(JobOffer offer);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.workType")
    @Mapping(target = "workerName", ignore = true)
    @Mapping(target = "workerAvatarUrl", ignore = true)
    @Mapping(target = "workerKycVerified", ignore = true)
    @Mapping(target = "workerRating", ignore = true)
    @Mapping(target = "workerMissionsCount", ignore = true)
    @Mapping(target = "compatibilityScore", ignore = true)
    ApplicationResponse toApplicationResponse(Application application);

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "farmerName", ignore = true)
    @Mapping(target = "workerName", ignore = true)
    @Mapping(target = "fullySignedByBoth", expression = "java(contract.isFullySigned())")
    ContractResponse toContractResponse(Contract contract);

    @Mapping(target = "contractId", source = "contract.id")
    @Mapping(target = "farmerName", ignore = true)
    @Mapping(target = "workerName", ignore = true)
    MissionResponse toMissionResponse(Mission mission);

    default Location mapLocation(CreateJobOfferRequest req) {
        if (req.getLatitude() == null || req.getLongitude() == null) return null;
        return Location.builder()
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();
    }
}
