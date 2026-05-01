package com.agriconnect.labor.service.impl;

import com.agriconnect.commons.exception.BusinessException;
import com.agriconnect.commons.exception.ForbiddenException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.labor.domain.entity.Application;
import com.agriconnect.labor.domain.entity.Contract;
import com.agriconnect.labor.domain.entity.Mission;
import com.agriconnect.labor.domain.enums.ApplicationStatus;
import com.agriconnect.labor.domain.enums.ContractStatus;
import com.agriconnect.labor.domain.enums.MissionStatus;
import com.agriconnect.labor.dto.request.SignContractRequest;
import com.agriconnect.labor.dto.response.ContractResponse;
import com.agriconnect.labor.event.model.ContractSignedEvent;
import com.agriconnect.labor.event.publisher.LaborEventPublisher;
import com.agriconnect.labor.mapper.LaborMapper;
import com.agriconnect.labor.repository.ApplicationRepository;
import com.agriconnect.labor.repository.ContractRepository;
import com.agriconnect.labor.repository.MissionRepository;
import com.agriconnect.labor.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ApplicationRepository applicationRepository;
    private final MissionRepository missionRepository;
    private final LaborMapper laborMapper;
    private final LaborEventPublisher eventPublisher;

    @Override
    @Transactional
    public ContractResponse generateContract(UUID applicationId, UUID farmerId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Candidature", applicationId.toString()));

        if (!application.getJob().getFarmerId().equals(farmerId)) {
            throw new ForbiddenException("Accès refusé");
        }
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new BusinessException("La candidature doit être acceptée avant de générer un contrat");
        }
        if (contractRepository.findByApplication_Id(applicationId).isPresent()) {
            throw new BusinessException("Un contrat existe déjà pour cette candidature");
        }

        Contract contract = Contract.builder()
                .job(application.getJob())
                .application(application)
                .farmerId(farmerId)
                .workerId(application.getWorkerId())
                .amountFcfa(application.getJob().getSalaryFcfa()
                        * application.getJob().getDurationDays())
                .durationDays(application.getJob().getDurationDays())
                .workType(application.getJob().getWorkType().name())
                .locationText(application.getJob().getAddressText())
                .startDate(application.getJob().getStartDate())
                .status(ContractStatus.DRAFT)
                .build();

        contract = contractRepository.save(contract);
        log.info("Contrat généré: id={} appId={}", contract.getId(), applicationId);
        return laborMapper.toContractResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse getById(UUID id, UUID requesterId) {
        Contract contract = findContractById(id);
        checkContractAccess(contract, requesterId);
        return laborMapper.toContractResponse(contract);
    }

    @Override
    @Transactional
    public ContractResponse sign(UUID contractId, UUID signerId, SignContractRequest request) {
        Contract contract = findContractById(contractId);
        checkContractAccess(contract, signerId);

        if (contract.getStatus() == ContractStatus.SIGNED
                || contract.getStatus() == ContractStatus.ACTIVE) {
            throw new BusinessException("Ce contrat est déjà signé");
        }

        boolean isFarmer = contract.getFarmerId().equals(signerId);
        boolean isWorker = contract.getWorkerId().equals(signerId);

        if (isFarmer && contract.getFarmerSignedAt() != null) {
            throw new BusinessException("Vous avez déjà signé ce contrat");
        }
        if (isWorker && contract.getWorkerSignedAt() != null) {
            throw new BusinessException("Vous avez déjà signé ce contrat");
        }

        LocalDateTime now = LocalDateTime.now();
        if (isFarmer) contract.setFarmerSignedAt(now);
        if (isWorker) contract.setWorkerSignedAt(now);
        contract.setUpdatedAt(now);

        if (contract.isFullySigned()) {
            contract.setStatus(ContractStatus.SIGNED);

            // Créer la mission automatiquement
            Mission mission = Mission.builder()
                    .contract(contract)
                    .farmerId(contract.getFarmerId())
                    .workerId(contract.getWorkerId())
                    .status(MissionStatus.SCHEDULED)
                    .scheduledDate(contract.getStartDate())
                    .build();
            missionRepository.save(mission);

            // Publier l'événement pour bloquer le paiement en escrow
            eventPublisher.publishContractSigned(ContractSignedEvent.builder()
                    .contractId(contract.getId())
                    .farmerId(contract.getFarmerId())
                    .workerId(contract.getWorkerId())
                    .amountFcfa(contract.getAmountFcfa())
                    .build());

            log.info("Contrat signé par les deux parties: id={}", contractId);
        }

        contract = contractRepository.save(contract);
        return laborMapper.toContractResponse(contract);
    }

    private Contract findContractById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contrat", id.toString()));
    }

    private void checkContractAccess(Contract contract, UUID userId) {
        if (!contract.getFarmerId().equals(userId) && !contract.getWorkerId().equals(userId)) {
            throw new ForbiddenException("Accès au contrat refusé");
        }
    }
}
