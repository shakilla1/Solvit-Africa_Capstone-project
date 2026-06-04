package com.phishguard.api.service.impl;

import com.phishguard.api.entity.*;
import com.phishguard.api.enums.*;
import com.phishguard.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepo;
    private final CampaignTargetRepository targetRepo;
    private final AdminUserRepository adminRepo;
    private final TargetUserRepository userRepo;
    private final EmailTemplateRepository templateRepo;
    private final SimulationEventRepository eventRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ── Create Campaign ────────────────────────────────────
    @Transactional
    public Campaign createCampaign(Long adminId, String name, String description,
                                   String difficulty, Long templateId,
                                   boolean autoRetest, int retestDays,
                                   List<Map<String, String>> targetList) {
        AdminUser admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Plan check: campaign limit
        long existing = campaignRepo.countByAdminId(adminId);
        if (existing >= admin.getPlan().getMaxCampaigns()) {
            throw new RuntimeException("Campaign limit reached for your plan (" +
                    admin.getPlan().name() + "). Please upgrade.");
        }

        EmailTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        Campaign campaign = Campaign.builder()
                .name(name)
                .description(description)
                .difficulty(Difficulty.valueOf(difficulty.toUpperCase()))
                .status(CampaignStatus.DRAFT)
                .admin(admin)
                .template(template)
                .autoRetest(autoRetest)
                .retestIntervalDays(retestDays > 0 ? retestDays : 14)
                .build();

        campaign = campaignRepo.save(campaign);

        // Create or find target users and assign tracking tokens
        List<CampaignTarget> targets = new ArrayList<>();
        for (Map<String, String> t : targetList) {
            String email = t.get("email");
            if (email == null || email.isBlank()) continue;

            TargetUser user = userRepo.findByEmail(email).orElseGet(() -> {
                TargetUser newUser = TargetUser.builder()
                        .fullName(t.getOrDefault("name", email))
                        .email(email)
                        .department(t.getOrDefault("department", ""))
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .admin(admin)
                        .build();
                return userRepo.save(newUser);
            });

            CampaignTarget ct = CampaignTarget.builder()
                    .campaign(campaign)
                    .targetUser(user)
                    .trackingToken(UUID.randomUUID().toString())
                    .build();
            targets.add(ct);
        }

        targetRepo.saveAll(targets);
        campaign.setTotalTargets(targets.size());
        return campaignRepo.save(campaign);
    }

    // ── Launch Campaign (smart scheduling) ────────────────
    @Transactional
    public Campaign launchCampaign(Long campaignId, Long adminId) {
        Campaign campaign = campaignRepo.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));

        if (!campaign.getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Access denied");
        }

        List<CampaignTarget> targets = targetRepo.findByCampaignId(campaignId);
        if (targets.isEmpty()) throw new RuntimeException("No targets in this campaign");

        // For presentations: immediate send
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(campaign.getSendWindowHours());

        for (CampaignTarget target : targets) {
            target.setScheduledSendAt(now);
        }

        targetRepo.saveAll(targets);

        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setLaunchedAt(now);
        campaign.setScheduledStartAt(now);
        campaign.setScheduledEndAt(windowEnd);

        if (campaign.isAutoRetest()) {
            campaign.setNextRetestAt(now.plusDays(campaign.getRetestIntervalDays()));
        }

        Campaign saved = campaignRepo.save(campaign);

        // Send emails immediately for demo/presentation
        processPendingEmails();

        // Notify admin
        notificationService.createAdminNotification(
                adminId,
                NotificationCategory.CAMPAIGN,
                "Campaign Launched",
                "\"" + campaign.getName() + "\" is now active. " +
                        targets.size() + " emails sent immediately.",
                "/admin/campaigns/" + campaignId
        );

        return saved;
    }

    // ── Scheduled: Send pending emails ────────────────────
    @Scheduled(fixedDelay = 60_000) // every minute
    @Transactional
    public void processPendingEmails() {
        List<CampaignTarget> pending = targetRepo.findPendingSends(LocalDateTime.now());
        for (CampaignTarget ct : pending) {
            try {
                emailService.sendPhishingEmail(ct);
                ct.setEmailSent(true);
                ct.setEmailSentAt(LocalDateTime.now());
                targetRepo.save(ct);

                Campaign c = ct.getCampaign();
                c.setEmailsSent(c.getEmailsSent() + 1);
                campaignRepo.save(c);

                log.info("Sent phishing email to {} for campaign {}", ct.getTargetUser().getEmail(), c.getName());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", ct.getTargetUser().getEmail(), e.getMessage());
            }
        }
    }

    // ── Record Click ───────────────────────────────────────
    @Transactional
    public Map<String, Object> recordClick(String token, String ip, String userAgent) {
        CampaignTarget ct = targetRepo.findByTrackingToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid tracking token"));

        if (!ct.isLinkClicked()) {
            ct.setLinkClicked(true);
            ct.setClickedAt(LocalDateTime.now());
            targetRepo.save(ct);

            Campaign c = ct.getCampaign();
            c.setLinksClicked(c.getLinksClicked() + 1);
            campaignRepo.save(c);

            // Record event
            SimulationEvent event = SimulationEvent.builder()
                    .campaign(c)
                    .targetUser(ct.getTargetUser())
                    .eventType(EventType.LINK_CLICKED)
                    .trackingToken(token)
                    .ipAddress(ip)
                    .userAgent(userAgent)
                    .build();
            eventRepo.save(event);

            // Update risk score
            TargetUser user = ct.getTargetUser();
            user.setRiskScore(Math.min(100, user.getRiskScore() + 20));
            userRepo.save(user);

            // Notify admin
            notificationService.createAdminNotification(
                    c.getAdmin().getId(),
                    NotificationCategory.CAMPAIGN,
                    "Link Clicked",
                    user.getFullName() + " clicked a phishing link in \"" + c.getName() + "\"",
                    "/admin/campaigns/" + c.getId()
            );
        }

        return Map.of(
                "campaignName", ct.getCampaign().getName(),
                "landingType", ct.getCampaign().getTemplate().getLandingPageType()
        );
    }

    // ── Record Credential Submission ───────────────────────
    @Transactional
    public Map<String, Object> recordCredentials(String token, String username,
                                                  String rawPassword, String landingType,
                                                  String ip, String userAgent) {
        CampaignTarget ct = targetRepo.findByTrackingToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid tracking token"));

        ct.setCredentialsSubmitted(true);
        ct.setSubmittedAt(LocalDateTime.now());
        targetRepo.save(ct);

        Campaign c = ct.getCampaign();
        c.setCredentialsSubmitted(c.getCredentialsSubmitted() + 1);
        campaignRepo.save(c);

        // Mask password for display-back (show first char + stars)
        String masked = rawPassword.length() > 0
                ? rawPassword.charAt(0) + "*".repeat(rawPassword.length() - 1)
                : "****";

        SimulationEvent event = SimulationEvent.builder()
                .campaign(c)
                .targetUser(ct.getTargetUser())
                .eventType(EventType.CREDENTIALS_SUBMITTED)
                .trackingToken(token)
                .ipAddress(ip)
                .userAgent(userAgent)
                .capturedUsername(username)
                .capturedPasswordHash(passwordEncoder.encode(rawPassword)) // never store plain
                .capturedPasswordMasked(masked)
                .landingPageType(landingType)
                .build();
        eventRepo.save(event);

        TargetUser user = ct.getTargetUser();
        user.setRiskScore(Math.min(100, user.getRiskScore() + 35));
        userRepo.save(user);

        notificationService.createAdminNotification(
                c.getAdmin().getId(),
                NotificationCategory.CAMPAIGN,
                "Credentials Submitted ⚠️",
                user.getFullName() + " submitted credentials on a fake " + landingType + " page in \"" + c.getName() + "\"",
                "/admin/campaigns/" + c.getId()
        );

        // Return data for display-back to victim
        return Map.of(
                "username", username,
                "passwordMasked", masked,
                "landingType", landingType,
                "capturedAt", LocalDateTime.now().toString(),
                "ipAddress", ip
        );
    }

    // ── Pause / Resume ─────────────────────────────────────
    @Transactional
    public Campaign togglePause(Long campaignId, Long adminId) {
        Campaign c = campaignRepo.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        if (!c.getAdmin().getId().equals(adminId)) throw new RuntimeException("Access denied");

        c.setStatus(c.getStatus() == CampaignStatus.PAUSED ? CampaignStatus.ACTIVE : CampaignStatus.PAUSED);
        return campaignRepo.save(c);
    }

    // ── Delete ─────────────────────────────────────────────
    @Transactional
    public void deleteCampaign(Long campaignId, Long adminId) {
        Campaign c = campaignRepo.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        if (!c.getAdmin().getId().equals(adminId)) throw new RuntimeException("Access denied");
        campaignRepo.delete(c);
    }

    // ── Get all for admin ──────────────────────────────────
    public List<Campaign> getCampaigns(Long adminId) {
        return campaignRepo.findByAdminId(adminId);
    }

    public Campaign getCampaign(Long id, Long adminId) {
        Campaign c = campaignRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found"));
        if (!c.getAdmin().getId().equals(adminId)) throw new RuntimeException("Access denied");
        return c;
    }
}
