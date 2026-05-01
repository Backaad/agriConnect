package com.agriconnect.notification.repository;

import com.agriconnect.notification.domain.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    List<DeviceToken> findByUserIdAndActiveTrue(UUID userId);

    Optional<DeviceToken> findByFcmToken(String fcmToken);

    boolean existsByFcmToken(String fcmToken);

    @Modifying
    @Query("UPDATE DeviceToken d SET d.active = false WHERE d.fcmToken = :token")
    void deactivateToken(@Param("token") String fcmToken);

    @Modifying
    @Query("UPDATE DeviceToken d SET d.active = false WHERE d.userId = :userId")
    void deactivateAllUserTokens(@Param("userId") UUID userId);

    @Query("SELECT COUNT(d) FROM DeviceToken d WHERE d.userId = :userId AND d.active = true")
    long countActiveByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE DeviceToken d SET d.active = false WHERE d.lastSeen < :cutoff AND d.active = true")
    int deactivateStaleTokens(@Param("cutoff") LocalDateTime cutoff);
}
