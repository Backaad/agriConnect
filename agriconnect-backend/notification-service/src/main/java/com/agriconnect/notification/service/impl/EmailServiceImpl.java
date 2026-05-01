package com.agriconnect.notification.service.impl;

import com.agriconnect.notification.service.EmailService;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name:AgriConnect}")
    private String fromName;

    @Value("${sendgrid.enabled:false}")
    private boolean sendgridEnabled;

    @Override
    public boolean sendEmail(String toEmail, String toName, String subject, String htmlBody) {
        if (!sendgridEnabled) {
            log.warn("[DEV] Email simulé → to={} subject='{}'", toEmail, subject);
            return true;
        }
        try {
            Email from    = new Email(fromEmail, fromName);
            Email to      = new Email(toEmail, toName);
            Content content = new Content("text/html", htmlBody);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email envoyé: to={} subject='{}'", toEmail, subject);
                return true;
            }
            log.warn("SendGrid status={}: {}", response.getStatusCode(), response.getBody());
            return false;
        } catch (IOException e) {
            log.error("Email erreur: to={} err={}", toEmail, e.getMessage());
            return false;
        }
    }
}
