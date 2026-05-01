package com.agriconnect.auth.service.impl;

import com.agriconnect.auth.config.TwilioConfig;
import com.agriconnect.auth.domain.enums.UserStatus;
import com.agriconnect.auth.dto.request.OtpSendRequest;
import com.agriconnect.auth.dto.request.OtpVerifyRequest;
import com.agriconnect.auth.dto.response.OtpResponse;
import com.agriconnect.auth.exception.OtpException;
import com.agriconnect.auth.repository.UserRepository;
import com.agriconnect.auth.service.OtpService;
import com.agriconnect.commons.util.PhoneUtils;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final TwilioConfig twilioConfig;

    @Value("${otp.ttl-minutes:5}")
    private int otpTtlMinutes;

    @Value("${otp.max-attempts:3}")
    private int maxAttempts;

    private static final String OTP_PREFIX      = "otp:code:";
    private static final String ATTEMPTS_PREFIX = "otp:attempts:";
    private static final SecureRandom RANDOM    = new SecureRandom();

    @Override
    public OtpResponse sendOtp(OtpSendRequest request) {
        String phone = PhoneUtils.normalize(request.getPhone());

        if (!PhoneUtils.isValidCameroonPhone(phone)) {
            throw new OtpException("Numéro de téléphone invalide", "INVALID_PHONE");
        }

        // Vérifier si un OTP récent existe déjà (anti-spam)
        String existingAttempts = redisTemplate.opsForValue().get(ATTEMPTS_PREFIX + phone);
        if (existingAttempts != null && Integer.parseInt(existingAttempts) >= maxAttempts) {
            throw new OtpException(
                "Trop de tentatives. Réessayez dans " + otpTtlMinutes + " minutes.",
                "TOO_MANY_ATTEMPTS"
            );
        }

        String code = generateOtp();
        String message = "AgriConnect: Votre code de vérification est " + code +
                ". Valable " + otpTtlMinutes + " minutes. Ne le partagez pas.";

        // Stocker le code dans Redis
        redisTemplate.opsForValue().set(OTP_PREFIX + phone, code, Duration.ofMinutes(otpTtlMinutes));
        redisTemplate.opsForValue().increment(ATTEMPTS_PREFIX + phone);
        redisTemplate.expire(ATTEMPTS_PREFIX + phone, Duration.ofMinutes(otpTtlMinutes));

        // Envoyer via Twilio
        if (twilioConfig.isEnabled()) {
            try {
                Message.creator(
                    new PhoneNumber(phone),
                    new PhoneNumber(twilioConfig.getFromNumber()),
                    message
                ).create();
                log.info("OTP envoyé avec succès à {}", PhoneUtils.mask(phone));
            } catch (Exception e) {
                log.error("Erreur Twilio: {}", e.getMessage(), e);
                throw new OtpException("Impossible d'envoyer le SMS. Réessayez.", "SMS_SEND_FAILED");
            }
        } else {
            // Mode développement — afficher dans les logs
            log.warn("[DEV MODE] OTP pour {}: {}", PhoneUtils.mask(phone), code);
        }

        return OtpResponse.builder()
                .sent(true)
                .maskedPhone(PhoneUtils.mask(phone))
                .channel(request.getChannel())
                .expiresInMinutes(otpTtlMinutes)
                .message("Code envoyé sur " + PhoneUtils.mask(phone))
                .build();
    }

    @Override
    @Transactional
    public boolean verifyOtp(OtpVerifyRequest request) {
        String phone = PhoneUtils.normalize(request.getPhone());
        String storedCode = redisTemplate.opsForValue().get(OTP_PREFIX + phone);

        if (storedCode == null) {
            throw new OtpException("Code OTP expiré ou introuvable. Demandez un nouveau code.", "OTP_EXPIRED");
        }
        if (!storedCode.equals(request.getCode())) {
            throw new OtpException("Code OTP incorrect", "OTP_INVALID");
        }

        // Supprimer le code après vérification réussie
        redisTemplate.delete(OTP_PREFIX + phone);
        redisTemplate.delete(ATTEMPTS_PREFIX + phone);

        // Activer le compte
        markPhoneVerified(phone);
        log.info("OTP vérifié avec succès pour {}", PhoneUtils.mask(phone));
        return true;
    }

    @Override
    @Transactional
    public void markPhoneVerified(String phone) {
        userRepository.findByPhone(phone).ifPresent(user -> {
            if (user.getStatus() == UserStatus.PENDING) {
                userRepository.updateStatus(user.getId(), UserStatus.ACTIVE);
            }
        });
    }

    private String generateOtp() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
