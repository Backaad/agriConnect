package com.agriconnect.notification.service.impl;

import com.agriconnect.notification.domain.entity.DeviceToken;
import com.agriconnect.notification.repository.DeviceTokenRepository;
import com.agriconnect.notification.repository.NotificationLogRepository;
import com.agriconnect.notification.domain.entity.NotificationLog;
import com.agriconnect.notification.domain.enums.DeliveryStatus;
import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.service.PushNotificationService;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationServiceImpl implements PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationLogRepository logRepository;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @Override
    public boolean sendToUser(UUID userId, String title, String body, Map<String, String> data) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUserIdAndActiveTrue(userId);
        if (tokens.isEmpty()) {
            log.debug("Aucun device actif pour userId={}", userId);
            return false;
        }

        boolean atLeastOneSent = false;
        for (DeviceToken device : tokens) {
            if (sendToToken(device.getFcmToken(), title, body, data)) {
                atLeastOneSent = true;
                device.updateLastSeen();
                deviceTokenRepository.save(device);
            }
        }
        return atLeastOneSent;
    }

    @Override
    public boolean sendToToken(String token, String title, String body, Map<String, String> data) {
        if (!firebaseEnabled) {
            log.warn("[DEV] FCM push simulé → title='{}' body='{}'", title, body);
            return true;
        }
        try {
            Message.Builder builder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build());

            if (data != null && !data.isEmpty()) {
                builder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("FCM envoyé: messageId={}", messageId);

            logRepository.save(NotificationLog.builder()
                    .userId(UUID.randomUUID()) // sera enrichi par l'appelant
                    .channel(NotificationChannel.PUSH)
                    .providerRef(messageId)
                    .title(title)
                    .status(DeliveryStatus.SENT)
                    .build());
            return true;

        } catch (FirebaseMessagingException e) {
            log.error("FCM erreur: code={} msg={}", e.getMessagingErrorCode(), e.getMessage());

            // Désactiver le token si invalide
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                    || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                deviceTokenRepository.deactivateToken(token);
                log.info("Token FCM désactivé (invalide): {}", token.substring(0, 20) + "...");
            }
            return false;
        }
    }

    @Override
    public void sendToMultipleUsers(List<UUID> userIds, String title, String body) {
        List<String> tokens = userIds.stream()
                .flatMap(uid -> deviceTokenRepository.findByUserIdAndActiveTrue(uid).stream())
                .map(DeviceToken::getFcmToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) return;

        if (!firebaseEnabled) {
            log.warn("[DEV] FCM multicast simulé → {} tokens", tokens.size());
            return;
        }

        // Envoyer par lots de 500 (limite FCM)
        int batchSize = 500;
        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            try {
                MulticastMessage message = MulticastMessage.builder()
                        .addAllTokens(batch)
                        .setNotification(Notification.builder()
                                .setTitle(title).setBody(body).build())
                        .build();
                BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
                log.info("FCM multicast: {} succès / {} total",
                        response.getSuccessCount(), batch.size());
            } catch (FirebaseMessagingException e) {
                log.error("FCM multicast erreur: {}", e.getMessage());
            }
        }
    }
}
