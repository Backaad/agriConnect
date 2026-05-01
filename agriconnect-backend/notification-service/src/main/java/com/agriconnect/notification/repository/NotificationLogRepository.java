package com.agriconnect.notification.repository;

import com.agriconnect.notification.domain.entity.NotificationLog;
import com.agriconnect.notification.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    List<NotificationLog> findByStatus(DeliveryStatus status);
    List<NotificationLog> findByUserId(UUID userId);
}
