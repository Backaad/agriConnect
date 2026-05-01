package com.agriconnect.notification.event.listener;

import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.event.model.*;
import com.agriconnect.notification.service.NotificationRouter;
import com.agriconnect.notification.repository.DeviceTokenRepository;
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
public class PaymentEventListener {

    private final NotificationRouter router;
    private final DeviceTokenRepository deviceTokenRepository;

    @KafkaListener(topics = "payment.success", groupId = "notification-service")
    public void onPaymentSuccess(ConsumerRecord<String, PaymentSuccessEvent> record, Acknowledgment ack) {
        try {
            PaymentSuccessEvent event = record.value();
            // Retrouver l'userId depuis le walletId via lookup
            // Pour l'instant, on logue et on acknowledge
            log.info("Payment success reçu: ref={} amount={}", event.getTransactionRef(), event.getAmountFcfa());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur PaymentSuccess: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service")
    public void onPaymentFailed(ConsumerRecord<String, PaymentFailedEvent> record, Acknowledgment ack) {
        try {
            PaymentFailedEvent event = record.value();
            log.warn("Payment failed reçu: ref={} reason={}", event.getTransactionRef(), event.getReason());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur PaymentFailed: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.escrow.released", groupId = "notification-service")
    public void onEscrowReleased(ConsumerRecord<String, EscrowReleasedEvent> record, Acknowledgment ack) {
        try {
            EscrowReleasedEvent event = record.value();
            Map<String, String> p = new HashMap<>();
            if (event.getAmountFcfa() != null) p.put("amount", String.valueOf(event.getAmountFcfa()));
            // Notifier le bénéficiaire
            router.route(event.getPayeeId(), NotificationType.ESCROW_RELEASED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur EscrowReleased: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "marketplace.order.delivered", groupId = "notification-service")
    public void onOrderDelivered(ConsumerRecord<String, OrderDeliveredEvent> record, Acknowledgment ack) {
        try {
            OrderDeliveredEvent event = record.value();
            Map<String, String> p = Map.of("orderId", event.getOrderId() != null ? event.getOrderId().toString() : "");
            router.route(event.getConsumerId(), NotificationType.ORDER_DELIVERED, p);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erreur OrderDelivered: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
