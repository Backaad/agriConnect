package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.JobOffer;
import com.agriconnect.labor.domain.enums.JobStatus;
import com.agriconnect.labor.domain.enums.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, UUID> {

    Page<JobOffer> findByFarmerId(UUID farmerId, Pageable pageable);

    Page<JobOffer> findByStatus(JobStatus status, Pageable pageable);

    Page<JobOffer> findByStatusAndWorkType(JobStatus status, WorkType workType, Pageable pageable);

    @Query("SELECT j FROM JobOffer j " +
           "WHERE j.status = 'OPEN' " +
           "AND (j.expiresAt IS NULL OR j.expiresAt > CURRENT_TIMESTAMP) " +
           "AND (:workType IS NULL OR j.workType = :workType) " +
           "AND (:minSalary IS NULL OR j.salaryFcfa >= :minSalary) " +
           "AND (:fromDate IS NULL OR j.startDate >= :fromDate) " +
           "ORDER BY j.createdAt DESC")
    Page<JobOffer> findOpenOffers(
        @Param("workType") WorkType workType,
        @Param("minSalary") Long minSalary,
        @Param("fromDate") LocalDate fromDate,
        Pageable pageable
    );

    /**
     * Recherche par proximité géographique via PostGIS.
     * Retourne les offres dans un rayon donné (en km) autour de lat/lng.
     */
    @Query(value = "SELECT * FROM job_offers j " +
           "WHERE j.status = 'OPEN' " +
           "AND (j.expires_at IS NULL OR j.expires_at > NOW()) " +
           "AND ST_DWithin(j.location::geography, ST_MakePoint(:lng, :lat)::geography, :radiusMeters) " +
           "ORDER BY ST_Distance(j.location::geography, ST_MakePoint(:lng, :lat)::geography) ASC",
           nativeQuery = true)
    List<JobOffer> findNearby(
        @Param("lat") double lat,
        @Param("lng") double lng,
        @Param("radiusMeters") int radiusMeters,
        Pageable pageable
    );

    long countByFarmerIdAndStatus(UUID farmerId, JobStatus status);
}
