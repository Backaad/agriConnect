package com.agriconnect.notification.event.listener;

import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.event.model.KycStatusEvent;
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
public class KycEventListener {

    private final NotificationRouter router;

    @KafkaListener(topics = {"kyc.approved", "kyc.rejected"}, groupId = "notification-service")
    public void onKycStatusChanged(ConsumerRecord<String, KycStatusEvent> record, Acknowledgment ack) {
        try {
            KycStatusEvent event = record.value();
            log.info("KYC event reçu: userId={} status={}", event.getUserId(), event.getStatus());

            NotificationType type = "APPROVED".equalsIgnoreCase(event.getStatus())
                    ? NotificationType.KYC_APPROVED
                    : NotificationType.KYC_REJECTED;

            Map<String, String> params = new HashMap<>();
            if (event.getReason() != null) params.put("reason", event.getReason());

            router.route(event.getUserId(), type, params);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur KYC event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
