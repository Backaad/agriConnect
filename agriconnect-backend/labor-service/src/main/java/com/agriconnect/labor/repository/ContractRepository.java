package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByApplicationId(Long applicationId);
}
