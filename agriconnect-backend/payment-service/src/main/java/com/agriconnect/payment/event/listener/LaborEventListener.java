package com.agriconnect.payment.event.listener;

import com.agriconnect.payment.dto.request.EscrowLockRequest;
import com.agriconnect.payment.dto.request.EscrowReleaseRequest;
import com.agriconnect.payment.event.model.ContractSignedEvent;
import com.agriconnect.payment.event.model.MissionCompletedEvent;
import com.agriconnect.payment.event.model.MissionDisputedEvent;
import com.agriconnect.payment.service.EscrowService;
import com.agriconnect.payment.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaborEventListener {

    private final EscrowService escrowService;
    private final WalletService walletService;

    /**
     * Quand le contrat est signé par les deux parties :
     * → Bloquer le montant en escrow sur le wallet du farmer
     */
    @KafkaListener(topics = "labor.contract.signed", groupId = "payment-service")
    public void onContractSigned(ContractSignedEvent event) {
        log.info("ContractSigned reçu: contractId={} amount={} FCFA",
                event.getContractId(), event.getAmountFcfa());
        try {
            walletService.ensureWalletExists(event.getFarmerId());
            walletService.ensureWalletExists(event.getWorkerId());

            EscrowLockRequest lockReq = new EscrowLockRequest();
            lockReq.setReferenceId(event.getContractId());
            lockReq.setReferenceType("LABOR_CONTRACT");
            lockReq.setPayerId(event.getFarmerId());
            lockReq.setPayeeId(event.getWorkerId());
            lockReq.setAmountFcfa(event.getAmountFcfa());

            escrowService.lock(lockReq);
            log.info("Escrow verrouillé pour contrat: {}", event.getContractId());
        } catch (Exception e) {
            log.error("Erreur lock escrow pour contrat {}: {}", event.getContractId(), e.getMessage(), e);
        }
    }

    /**
     * Quand la mission est validée par les deux parties :
     * → Libérer l'escrow vers le worker
     */
    @KafkaListener(topics = "labor.mission.completed", groupId = "payment-service")
    public void onMissionCompleted(MissionCompletedEvent event) {
        log.info("MissionCompleted reçu: missionId={} contractId={} amount={} FCFA",
                event.getMissionId(), event.getContractId(), event.getAmountFcfa());
        try {
            EscrowReleaseRequest releaseReq = new EscrowReleaseRequest();
            releaseReq.setReferenceId(event.getContractId());
            releaseReq.setReason("Mission validée par les deux parties — missionId=" + event.getMissionId());

            escrowService.release(releaseReq);
            log.info("Escrow libéré pour mission: {}", event.getMissionId());
        } catch (Exception e) {
            log.error("Erreur release escrow pour mission {}: {}", event.getMissionId(), e.getMessage(), e);
        }
    }

    /**
     * Quand un litige est ouvert :
     * → Geler l'escrow jusqu'à décision admin (ne rien faire d'autre)
     */
    @KafkaListener(topics = "labor.mission.disputed", groupId = "payment-service")
    public void onMissionDisputed(MissionDisputedEvent event) {
        log.warn("Litige ouvert sur missionId={} contractId={} — escrow gelé en attente admin",
                event.getMissionId(), event.getContractId());
        // L'escrow reste LOCKED jusqu'à décision admin qui appellera release ou refund
    }
}
