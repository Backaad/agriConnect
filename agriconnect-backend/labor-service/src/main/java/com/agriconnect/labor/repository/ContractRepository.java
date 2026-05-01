package com.agriconnect.labor.repository;

import com.agriconnect.labor.domain.entity.Contract;
import com.agriconnect.labor.domain.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    List<Contract> findByFarmerIdAndStatus(UUID farmerId, ContractStatus status);

    List<Contract> findByWorkerIdAndStatus(UUID workerId, ContractStatus status);

    Optional<Contract> findByApplication_Id(UUID applicationId);

    Page<Contract> findByFarmerId(UUID farmerId, Pageable pageable);

    Page<Contract> findByWorkerId(UUID workerId, Pageable pageable);
}
