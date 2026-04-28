package com.agriconnect.kyc.repository;

import com.agriconnect.kyc.domain.entity.KycApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycApplicationRepository extends JpaRepository<KycApplication, Long> {
    Optional<KycApplication> findByUserId(Long userId);
}
