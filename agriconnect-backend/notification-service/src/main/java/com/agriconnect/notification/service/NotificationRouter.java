package com.agriconnect.notification.service;

import com.agriconnect.notification.domain.enums.NotificationChannel;
import com.agriconnect.notification.domain.enums.NotificationType;
import com.agriconnect.notification.template.NotificationTemplates;

import java.util.Map;
import java.util.UUID;

public interface NotificationRouter {

    /**
     * Envoie une notification via le canal approprié selon les préférences
     * et type de notification, puis la persiste en base.
     */
    void route(UUID userId, NotificationType type, Map<String, String> params);

    /**
     * Envoi direct sur un canal spécifique.
     */
    void send(UUID userId, NotificationType type, NotificationChannel channel,
              Map<String, String> params);
}
