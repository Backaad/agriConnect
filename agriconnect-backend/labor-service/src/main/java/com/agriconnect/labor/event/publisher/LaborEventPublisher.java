package com.agriconnect.labor.event.publisher;

import com.agriconnect.labor.event.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LaborEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationAccepted(ApplicationAcceptedEvent event) {
        send("labor.application.accepted", event.getApplicationId().toString(), event);
    }

    public void publishContractSigned(ContractSignedEvent event) {
        send("labor.contract.signed", event.getContractId().toString(), event);
    }

    public void publishMissionCompleted(MissionCompletedEvent event) {
        send("labor.mission.completed", event.getMissionId().toString(), event);
    }

    public void publishMissionDisputed(MissionDisputedEvent event) {
        send("labor.mission.disputed", event.getMissionId().toString(), event);
    }

    private void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload).whenComplete((r, ex) -> {
            if (ex != null) log.error("Kafka send failed [{}]: {}", topic, ex.getMessage(), ex);
            else log.debug("Kafka sent [{}] key={}", topic, key);
        });
    }
}
