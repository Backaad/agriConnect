package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Application;
import com.agriconnect.labor.domain.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Page<Application> findByJob_Id(UUID jobId, Pageable pageable);

    Page<Application> findByJob_IdAndStatus(UUID jobId, ApplicationStatus status, Pageable pageable);

    Page<Application> findByWorkerId(UUID workerId, Pageable pageable);

    Optional<Application> findByJob_IdAndWorkerId(UUID jobId, UUID workerId);

    boolean existsByJob_IdAndWorkerId(UUID jobId, UUID workerId);

    long countByJob_Id(UUID jobId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.workerId = :wId AND a.status = 'ACCEPTED'")
    long countAcceptedByWorker(@Param("wId") UUID workerId);
}
