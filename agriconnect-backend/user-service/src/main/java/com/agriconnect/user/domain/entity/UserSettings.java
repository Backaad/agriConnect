package com.agriconnect.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(nullable = false, length = 10)
    private String language = "fr";

    @Builder.Default
    @Column(nullable = false)
    private boolean notifPush = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean notifSms = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean notifEmail = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean darkMode = false;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
