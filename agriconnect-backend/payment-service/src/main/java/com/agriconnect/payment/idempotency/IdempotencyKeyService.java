package com.agriconnect.payment.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyKeyService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "idempotency:payment:";
    private static final Duration TTL = Duration.ofHours(24);

    public String generateKey(UUID userId, String operation) {
        return userId + ":" + operation + ":" + System.currentTimeMillis();
    }

    /**
     * Vérifie si une clé a déjà été utilisée (prévention double-débit).
     * @return true si la clé est nouvelle (safe to proceed), false si déjà utilisée
     */
    public boolean tryAcquire(String key) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(PREFIX + key, "PROCESSING", TTL);
        boolean acquired = Boolean.TRUE.equals(result);
        if (!acquired) {
            log.warn("Idempotency key déjà utilisée: {}", key);
        }
        return acquired;
    }

    public void markCompleted(String key, String transactionRef) {
        redisTemplate.opsForValue().set(PREFIX + key, "DONE:" + transactionRef, TTL);
    }

    public String getExistingResult(String key) {
        String value = redisTemplate.opsForValue().get(PREFIX + key);
        if (value != null && value.startsWith("DONE:")) {
            return value.substring(5);
        }
        return null;
    }

    public void release(String key) {
        redisTemplate.delete(PREFIX + key);
    }
}
