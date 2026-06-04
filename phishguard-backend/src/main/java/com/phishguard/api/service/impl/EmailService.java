package com.phishguard.api.service.impl;

import com.phishguard.api.entity.CampaignTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Sends the phishing simulation email to a target.
     * When mailEnabled=false, logs the email instead of sending.
     * To enable real sending: set app.mail.enabled=true and configure SMTP.
     */
    public void sendPhishingEmail(CampaignTarget target) {
        String template = target.getCampaign().getTemplate().getHtmlBody();
        String token    = target.getTrackingToken();
        String name     = target.getTargetUser().getFullName();

// Replace placeholders in template - use frontend URLs for clickable links
         String body = template
                 .replace("{{NAME}}", name)
                 .replace("{{TRACKING_URL}}", frontendUrl + "/track/" + token)
                 .replace("{{LANDING_URL}}", frontendUrl + "/sim/" + token);

        String subject = target.getCampaign().getTemplate().getSubject();
        String to      = target.getTargetUser().getEmail();

        if (!mailEnabled) {
            log.info("[MOCK EMAIL] To: {} | Subject: {} | Token: {}", to, subject, token);
            return;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper  = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, target.getCampaign().getTemplate().getSenderName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Phishing email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }

    /** Generic transactional email (password reset, welcome, etc.) */
    public void sendTransactionalEmail(String to, String subject, String htmlBody) {
        if (!mailEnabled) {
            log.info("[MOCK EMAIL] To: {} | Subject: {}", to, subject);
            return;
        }
        try {
            var message = mailSender.createMimeMessage();
            var helper  = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, "PhishGuard Pro");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Transactional email sent to {}", to);
        } catch (Exception e) {
            log.error("Transactional email failed: {}", e.getMessage(), e);
            throw new RuntimeException("Email send failed: " + e.getMessage(), e);
        }
    }
}
