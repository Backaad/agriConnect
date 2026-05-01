package com.agriconnect.user.event.listener;

import com.agriconnect.user.domain.entity.UserProfile;
import com.agriconnect.user.domain.entity.UserRole;
import com.agriconnect.user.domain.entity.UserSettings;
import com.agriconnect.user.domain.enums.Role;
import com.agriconnect.user.event.model.UserRegisteredEvent;
import com.agriconnect.user.repository.UserProfileRepository;
import com.agriconnect.user.repository.UserRoleRepository;
import com.agriconnect.user.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventListener {

    private final UserProfileRepository profileRepository;
    private final UserRoleRepository roleRepository;
    private final UserSettingsRepository settingsRepository;

    @KafkaListener(topics = "user.registered", groupId = "user-service")
    @Transactional
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Événement UserRegistered reçu: userId={}", event.getUserId());

        if (!profileRepository.existsByUserId(event.getUserId())) {
            UserProfile profile = UserProfile.builder()
                    .userId(event.getUserId())
                    .firstName("")
                    .lastName("")
                    .build();
            profileRepository.save(profile);
        }

        String roleStr = event.getRole() != null ? event.getRole().toUpperCase() : "CONSUMER";
        try {
            Role role = Role.valueOf(roleStr);
            if (!roleRepository.existsByUserIdAndRole(event.getUserId(), role)) {
                roleRepository.save(UserRole.builder()
                        .userId(event.getUserId())
                        .role(role)
                        .build());
            }
        } catch (IllegalArgumentException e) {
            log.warn("Rôle inconnu reçu: {}", roleStr);
        }

        if (settingsRepository.findByUserId(event.getUserId()).isEmpty()) {
            settingsRepository.save(UserSettings.builder()
                    .userId(event.getUserId())
                    .build());
        }

        log.info("Profil utilisateur initialisé: userId={}", event.getUserId());
    }
}
