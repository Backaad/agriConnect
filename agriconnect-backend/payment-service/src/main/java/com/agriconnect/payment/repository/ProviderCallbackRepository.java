package com.agriconnect.payment.repository;

import com.agriconnect.payment.domain.entity.ProviderCallback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProviderCallbackRepository extends JpaRepository<ProviderCallback, UUID> {
    List<ProviderCallback> findByProcessedFalse();
}
