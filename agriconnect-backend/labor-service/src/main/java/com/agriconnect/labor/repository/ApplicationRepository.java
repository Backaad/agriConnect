package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByWorkerId(String workerId);
    List<Application> findByMissionId(Long missionId);
}
