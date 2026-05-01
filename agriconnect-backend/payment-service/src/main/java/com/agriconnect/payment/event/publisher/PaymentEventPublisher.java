package com.agriconnect.payment.event.publisher;

import com.agriconnect.payment.event.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentSuccess(PaymentSuccessEvent event) {
        send("payment.success", event.getTransactionRef(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        send("payment.failed", event.getTransactionRef(), event);
    }

    public void publishEscrowLocked(EscrowLockedEvent event) {
        send("payment.escrow.locked", event.getReferenceId().toString(), event);
    }

    public void publishEscrowReleased(EscrowReleasedEvent event) {
        send("payment.escrow.released", event.getReferenceId().toString(), event);
    }

    private void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload).whenComplete((r, ex) -> {
            if (ex != null) log.error("Kafka send failed [{}]: {}", topic, ex.getMessage(), ex);
            else log.debug("Kafka sent [{}] key={}", topic, key);
        });
    }
}
