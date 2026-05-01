package com.agriconnect.notification.domain.entity;

import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.domain.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(length = 200)
    private String providerRef;

    @Column(length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.SENT;

    @Column(columnDefinition = "TEXT")
    private String errorMsg;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt = LocalDateTime.now();
}
