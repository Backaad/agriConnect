package com.agriconnect.notification.service.impl;

import com.agriconnect.notification.domain.entity.NotificationRecord;
import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.repository.NotificationRepository;
import com.agriconnect.notification.service.EmailService;
import com.agriconnect.notification.service.NotificationRouter;
import com.agriconnect.notification.service.PushNotificationService;
import com.agriconnect.notification.service.SmsNotificationService;
import com.agriconnect.notification.template.NotificationTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRouterImpl implements NotificationRouter {

    private final PushNotificationService pushService;
    private final SmsNotificationService  smsService;
    private final EmailService            emailService;
    private final NotificationRepository  notificationRepository;

    @Override
    @Transactional
    public void route(UUID userId, NotificationType type, Map<String, String> params) {
        NotificationTemplates.NotificationTemplate tpl = NotificationTemplates.get(type, params);

        // Persister en base (IN_APP toujours)
        NotificationRecord record = NotificationRecord.builder()
                .userId(userId)
                .title(tpl.getTitle())
                .body(tpl.getBody())
                .type(type)
                .channel(NotificationChannel.PUSH)
                .data(params)
                .actionUrl(tpl.getActionUrl())
                .build();
        notificationRepository.save(record);

        // Envoi push (canal principal)
        boolean pushSent = pushService.sendToUser(userId, tpl.getTitle(), tpl.getBody(), params);

        // Fallback SMS pour les événements critiques si push échoue
        if (!pushSent && isCritical(type)) {
            String phone = params != null ? params.get("phone") : null;
            if (phone != null) {
                String smsBody = tpl.getTitle() + " — " + tpl.getBody();
                smsService.sendSms(phone, smsBody.length() > 160 ? smsBody.substring(0, 157) + "..." : smsBody);
            }
        }

        log.debug("Notification routée: userId={} type={} pushSent={}", userId, type, pushSent);
    }

    @Override
    @Transactional
    public void send(UUID userId, NotificationType type, NotificationChannel channel,
                     Map<String, String> params) {
        NotificationTemplates.NotificationTemplate tpl = NotificationTemplates.get(type, params);

        NotificationRecord record = NotificationRecord.builder()
                .userId(userId).title(tpl.getTitle()).body(tpl.getBody())
                .type(type).channel(channel).data(params).actionUrl(tpl.getActionUrl())
                .build();
        notificationRepository.save(record);

        switch (channel) {
            case PUSH  -> pushService.sendToUser(userId, tpl.getTitle(), tpl.getBody(), params);
            case SMS   -> {
                String phone = params != null ? params.get("phone") : null;
                if (phone != null) smsService.sendSms(phone, tpl.getBody());
            }
            case EMAIL -> {
                String email = params != null ? params.get("email") : null;
                String name  = params != null ? params.getOrDefault("name", "Utilisateur") : "Utilisateur";
                if (email != null) emailService.sendEmail(email, name, tpl.getTitle(), buildHtml(tpl));
            }
            case IN_APP -> log.debug("Notification IN_APP persistée pour userId={}", userId);
        }
    }

    private boolean isCritical(NotificationType type) {
        return switch (type) {
            case CONTRACT_SIGNED, MISSION_COMPLETED, PAYMENT_RECEIVED,
                 WITHDRAWAL_SUCCESS, ESCROW_RELEASED, KYC_APPROVED -> true;
            default -> false;
        };
    }

    private String buildHtml(NotificationTemplates.NotificationTemplate tpl) {
        String cta = tpl.getActionUrl() != null
            ? "<a href='https://app.agriconnect.cm" + tpl.getActionUrl()
              + "' style='display:inline-block;background:#E8941A;color:white;"
              + "padding:12px 24px;border-radius:8px;text-decoration:none;'>Voir maintenant</a>"
            : "";
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;'>"
            + "<div style='background:#1B6CA8;padding:20px;border-radius:8px 8px 0 0;'>"
            + "<h1 style='color:white;margin:0;font-size:24px;'>AgriConnect</h1></div>"
            + "<div style='background:#f5f7fa;padding:24px;border-radius:0 0 8px 8px;'>"
            + "<h2 style='color:#1A1A2E;'>" + tpl.getTitle() + "</h2>"
            + "<p style='color:#555;line-height:1.6;'>" + tpl.getBody() + "</p>"
            + cta
            + "<hr style='border:none;border-top:1px solid #ddd;margin:20px 0;'>"
            + "<p style='color:#999;font-size:12px;'>AgriConnect - Du champ a chez vous</p>"
            + "</div></body></html>";
    }
}
