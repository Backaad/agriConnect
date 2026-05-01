package com.agriconnect.notification.service.impl;

import com.agriconnect.notification.domain.entity.DeviceToken;
import com.agriconnect.notification.dto.request.RegisterDeviceRequest;
import com.agriconnect.notification.repository.DeviceTokenRepository;
import com.agriconnect.notification.service.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Value("${notification.max-devices-per-user:5}")
    private int maxDevicesPerUser;

    @Override
    @Transactional
    public void registerToken(UUID userId, RegisterDeviceRequest request) {
        // Si le token existe déjà, mettre à jour son propriétaire et last_seen
        deviceTokenRepository.findByFcmToken(request.getFcmToken()).ifPresentOrElse(
            existing -> {
                existing.setUserId(userId);
                existing.updateLastSeen();
                deviceTokenRepository.save(existing);
                log.debug("FCM token mis à jour pour userId={}", userId);
            },
            () -> {
                // Vérifier la limite de devices par utilisateur
                long activeCount = deviceTokenRepository.countActiveByUserId(userId);
                if (activeCount >= maxDevicesPerUser) {
                    // Désactiver le plus ancien
                    deviceTokenRepository.findByUserIdAndActiveTrue(userId).stream()
                            .min((a, b) -> a.getLastSeen().compareTo(b.getLastSeen()))
                            .ifPresent(oldest -> {
                                deviceTokenRepository.deactivateToken(oldest.getFcmToken());
                                log.info("Ancien device désactivé pour userId={}", userId);
                            });
                }
                DeviceToken token = DeviceToken.builder()
                        .userId(userId)
                        .fcmToken(request.getFcmToken())
                        .platform(request.getPlatform())
                        .deviceName(request.getDeviceName())
                        .build();
                deviceTokenRepository.save(token);
                log.info("Nouveau FCM token enregistré pour userId={}", userId);
            }
        );
    }

    @Override
    @Transactional
    public void unregisterToken(UUID userId, String fcmToken) {
        deviceTokenRepository.deactivateToken(fcmToken);
        log.info("FCM token désactivé pour userId={}", userId);
    }

    @Override
    @Transactional
    public void unregisterAllUserDevices(UUID userId) {
        deviceTokenRepository.deactivateAllUserTokens(userId);
        log.info("Tous les devices déconnectés pour userId={}", userId);
    }

    @Scheduled(cron = "0 0 4 * * ?") // chaque nuit à 4h
    @Transactional
    public void cleanStaleTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        int count = deviceTokenRepository.deactivateStaleTokens(cutoff);
        log.info("Tokens FCM inactifs désactivés: {}", count);
    }
}
