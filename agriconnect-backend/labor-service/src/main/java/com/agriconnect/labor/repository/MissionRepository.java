package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.MissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissionRepository extends JpaRepository<Mission, UUID> {

    Optional<Mission> findByContract_Id(UUID contractId);

    Page<Mission> findByFarmerId(UUID farmerId, Pageable pageable);

    Page<Mission> findByWorkerId(UUID workerId, Pageable pageable);

    Page<Mission> findByFarmerIdAndStatus(UUID farmerId, MissionStatus status, Pageable pageable);

    Page<Mission> findByWorkerIdAndStatus(UUID workerId, MissionStatus status, Pageable pageable);

    long countByWorkerIdAndStatus(UUID workerId, MissionStatus status);
}
