package com.agriconnect.auth.event.publisher;

import com.agriconnect.auth.event.model.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_USER_REGISTERED = "user.registered";

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("Publication de l'événement UserRegistered pour userId={}", event.getUserId());
        kafkaTemplate.send(TOPIC_USER_REGISTERED, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Échec publication UserRegistered: {}", ex.getMessage(), ex);
                    } else {
                        log.debug("UserRegistered publié sur partition={} offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
