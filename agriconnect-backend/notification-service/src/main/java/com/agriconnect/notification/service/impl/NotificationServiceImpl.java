package com.agriconnect.notification.service.impl;

import com.agriconnect.commons.dto.PageResponse;
import com.agriconnect.commons.exception.ForbiddenException;
import com.agriconnect.commons.exception.NotFoundException;
import com.agriconnect.notification.domain.entity.NotificationRecord;
import com.agriconnect.notification.dto.response.NotificationCountResponse;
import com.agriconnect.notification.dto.response.NotificationResponse;
import com.agriconnect.notification.mapper.NotificationMapper;
import com.agriconnect.notification.repository.NotificationRepository;
import com.agriconnect.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notifRepository;
    private final NotificationMapper notifMapper;

    @Value("${notification.history-retention-days:90}")
    private int retentionDays;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(UUID userId, boolean unreadOnly, Pageable pageable) {
        Page<NotificationRecord> page = unreadOnly
                ? notifRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                : notifRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(notifMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationCountResponse getCounts(UUID userId) {
        long unread = notifRepository.countByUserIdAndIsReadFalse(userId);
        long total  = notifRepository.findByUserIdOrderByCreatedAtDesc(
                userId, Pageable.ofSize(1)).getTotalElements();
        return NotificationCountResponse.builder()
                .unreadCount(unread)
                .totalCount(total)
                .build();
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        NotificationRecord notif = notifRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification", notificationId.toString()));
        if (!notif.getUserId().equals(userId)) {
            throw new ForbiddenException("Accès refusé à cette notification");
        }
        notif.markAsRead();
        notifRepository.save(notif);
    }

    @Override
    @Transactional
    public int markAllAsRead(UUID userId) {
        int count = notifRepository.markAllAsRead(userId, LocalDateTime.now());
        log.info("Toutes les notifications marquées comme lues: userId={} count={}", userId, count);
        return count;
    }

    @Override
    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        NotificationRecord notif = notifRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification", notificationId.toString()));
        if (!notif.getUserId().equals(userId)) {
            throw new ForbiddenException("Accès refusé");
        }
        notifRepository.delete(notif);
    }

    @Scheduled(cron = "0 0 3 * * ?") // chaque nuit à 3h
    @Transactional
    public void purgeOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        notifRepository.deleteOlderThan(cutoff);
        log.info("Purge notifications > {}j effectuée", retentionDays);
    }
}
