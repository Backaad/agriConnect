package com.agriconnect.notification.event.listener;

import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.event.model.UserRegisteredEvent;
import com.agriconnect.notification.service.NotificationRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventListener {

    private final NotificationRouter router;

    @KafkaListener(topics = "user.registered", groupId = "notification-service")
    public void onUserRegistered(ConsumerRecord<String, UserRegisteredEvent> record, Acknowledgment ack) {
        try {
            UserRegisteredEvent event = record.value();
            log.info("UserRegistered reçu: userId={}", event.getUserId());
            router.route(event.getUserId(), NotificationType.ACCOUNT_VERIFIED,
                    Map.of("phone", event.getPhone() != null ? event.getPhone() : ""));
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur traitement UserRegistered: {}", e.getMessage(), e);
            ack.acknowledge(); // Acknowledge to avoid infinite retry
        }
    }
}
