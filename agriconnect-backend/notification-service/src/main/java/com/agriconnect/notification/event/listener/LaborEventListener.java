package com.agriconnect.notification.event.listener;

import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.event.model.*;
import com.agriconnect.notification.service.NotificationRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaborEventListener {

    private final NotificationRouter router;

    @KafkaListener(topics = "labor.application.accepted", groupId = "notification-service")
    public void onApplicationAccepted(ConsumerRecord<String, ApplicationAcceptedEvent> record, Acknowledgment ack) {
        try {
            ApplicationAcceptedEvent event = record.value();
            Map<String, String> p = new HashMap<>();
            if (event.getJobTitle() != null) p.put("jobTitle", event.getJobTitle());
            p.put("farmerName", "L'agriculteur");
            p.put("applicationId", event.getApplicationId() != null ? event.getApplicationId().toString() : "");
            // Notifier le travailleur
            router.route(event.getWorkerId(), NotificationType.APPLICATION_ACCEPTED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur ApplicationAccepted: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "labor.contract.signed", groupId = "notification-service")
    public void onContractSigned(ConsumerRecord<String, ContractSignedEvent> record, Acknowledgment ack) {
        try {
            ContractSignedEvent event = record.value();
            Map<String, String> p = new HashMap<>();
            if (event.getJobTitle() != null) p.put("jobTitle", event.getJobTitle());
            if (event.getContractId() != null) p.put("contractId", event.getContractId().toString());
            if (event.getAmountFcfa() != null) p.put("amount", String.valueOf(event.getAmountFcfa()));
            // Notifier les deux parties
            router.route(event.getFarmerId(), NotificationType.CONTRACT_SIGNED, p);
            router.route(event.getWorkerId(), NotificationType.CONTRACT_SIGNED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur ContractSigned: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "labor.mission.completed", groupId = "notification-service")
    public void onMissionCompleted(ConsumerRecord<String, MissionCompletedEvent> record, Acknowledgment ack) {
        try {
            MissionCompletedEvent event = record.value();
            Map<String, String> p = new HashMap<>();
            if (event.getAmountFcfa() != null) p.put("amount", String.valueOf(event.getAmountFcfa()));
            if (event.getMissionId() != null) p.put("missionId", event.getMissionId().toString());
            router.route(event.getFarmerId(), NotificationType.MISSION_COMPLETED, p);
            router.route(event.getWorkerId(), NotificationType.MISSION_COMPLETED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur MissionCompleted: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "labor.mission.disputed", groupId = "notification-service")
    public void onMissionDisputed(ConsumerRecord<String, MissionDisputedEvent> record, Acknowledgment ack) {
        try {
            MissionDisputedEvent event = record.value();
            Map<String, String> p = Map.of("missionId", event.getMissionId() != null ? event.getMissionId().toString() : "");
            router.route(event.getFarmerId(), NotificationType.MISSION_DISPUTED, p);
            router.route(event.getWorkerId(), NotificationType.MISSION_DISPUTED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur MissionDisputed: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
