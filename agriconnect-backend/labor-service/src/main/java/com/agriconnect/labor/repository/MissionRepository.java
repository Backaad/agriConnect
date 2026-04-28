package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.MissionStatus;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    @Query("SELECT m FROM Mission m WHERE m.status = :status AND dwithin(m.location, :workerLocation, :radiusInDegrees) = true")
    List<Mission> findMissionsWithinRadius(
            @Param("workerLocation") Point workerLocation,
            @Param("radiusInDegrees") double radiusInDegrees,
            @Param("status") MissionStatus status
    );

    List<Mission> findByEmployerId(String employerId);
}
