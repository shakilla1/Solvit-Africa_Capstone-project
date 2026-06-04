package com.phishguard.api.controller;

import com.phishguard.api.entity.EmailTemplate;
import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.repository.*;
import com.phishguard.api.service.impl.TrainingService;
import com.phishguard.api.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AdminResourceController {

    private final EmailTemplateRepository templateRepo;
    private final AdminUserRepository adminRepo;
    private final TrainingService trainingService;
    private final CampaignRepository campaignRepo;
    private final TargetUserRepository targetUserRepo;
    private final SimulationEventRepository eventRepo;
    private final CampaignTargetRepository targetRepo;
    private final EmailService emailService;

    // ── Email Templates ────────────────────────────────────
    @GetMapping("/api/admin/templates")
    public ResponseEntity<List<EmailTemplate>> getTemplates(@AuthenticationPrincipal String email) {
        Long adminId = getAdminId(email);
        return ResponseEntity.ok(templateRepo.findByAdminIdOrIsDefaultTrue(adminId));
    }

    @GetMapping("/api/templates/public")
    public ResponseEntity<List<EmailTemplate>> getPublicTemplates() {
        return ResponseEntity.ok(templateRepo.findByIsDefaultTrue());
    }

    // ── Education Monitor ──────────────────────────────────
    @GetMapping("/api/admin/education/progress")
    public ResponseEntity<?> getEducationProgress(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(trainingService.getProgressForAdmin(getAdminId(email)));
    }

    // ── Target Users ───────────────────────────────────────
    @GetMapping("/api/admin/users")
    public ResponseEntity<List<Map<String, Object>>> getTargetUsers(@AuthenticationPrincipal String email) {
        Long adminId = getAdminId(email);
        List<TargetUser> users = targetUserRepo.findByAdminId(adminId);
        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("fullName", u.getFullName());
            m.put("email", u.getEmail());
            m.put("department", u.getDepartment());
            m.put("riskScore", u.getRiskScore());
            m.put("campaigns", targetRepo.countCampaignsByUserId(u.getId()));
            m.put("clicks", targetRepo.countClicksByUserId(u.getId()));
            m.put("submissions", targetRepo.countSubmissionsByUserId(u.getId()));
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/admin/users/{id}")
    public ResponseEntity<TargetUser> getTargetUser(@PathVariable Long id,
                                                     @AuthenticationPrincipal String email) {
        TargetUser user = targetUserRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    // ── Event Logs ─────────────────────────────────────────
    @GetMapping("/api/admin/events")
    public ResponseEntity<?> getEvents(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(eventRepo.findByCampaignAdminIdOrderByOccurredAtDesc(getAdminId(email)));
    }

    @GetMapping("/api/admin/campaigns/{id}/events")
    public ResponseEntity<?> getCampaignEvents(@PathVariable Long id) {
        return ResponseEntity.ok(eventRepo.findByCampaignIdOrderByOccurredAtDesc(id));
    }

    // ── Dashboard Stats ────────────────────────────────────
    @GetMapping("/api/admin/dashboard/stats")
    public ResponseEntity<?> getDashboardStats(@AuthenticationPrincipal String email) {
        Long adminId = getAdminId(email);
        var campaigns = campaignRepo.findByAdminId(adminId);

        int totalCampaigns = campaigns.size();
        int totalEmailsSent = campaigns.stream().mapToInt(c -> c.getEmailsSent()).sum();
        int totalClicks = campaigns.stream().mapToInt(c -> c.getLinksClicked()).sum();
        int totalSubmissions = campaigns.stream().mapToInt(c -> c.getCredentialsSubmitted()).sum();

        double clickRate = totalEmailsSent == 0 ? 0 :
                Math.round((double) totalClicks / totalEmailsSent * 1000.0) / 10.0;
        double submissionRate = totalEmailsSent == 0 ? 0 :
                Math.round((double) totalSubmissions / totalEmailsSent * 1000.0) / 10.0;

        Map<String, Long> statusCounts = campaigns.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()));

        List<TargetUser> atRisk = targetUserRepo.findTopAtRiskByAdminId(adminId)
                .stream().limit(5).collect(Collectors.toList());

        // Recent events across all campaigns
        List<Map<String, Object>> recentActivity = eventRepo
                .findByCampaignAdminIdOrderByOccurredAtDesc(adminId)
                .stream().limit(10).map(ev -> {
                    Map<String, Object> a = new HashMap<>();
                    a.put("user", ev.getTargetUser() != null ? ev.getTargetUser().getFullName() : "Unknown");
                    a.put("eventType", ev.getEventType().name());
                    a.put("occurredAt", ev.getOccurredAt());
                    return a;
                }).collect(Collectors.toList());

        // Campaign trend data (per campaign: click rate + submission rate)
        List<Map<String, Object>> trend = campaigns.stream()
                .filter(c -> c.getLaunchedAt() != null)
                .sorted(Comparator.comparing(c -> c.getLaunchedAt()))
                .map(c -> {
                    Map<String, Object> t = new HashMap<>();
                    t.put("name", c.getName());
                    t.put("clickRate", c.getEmailsSent() == 0 ? 0 :
                            Math.round((double) c.getLinksClicked() / c.getEmailsSent() * 100));
                    t.put("submissionRate", c.getEmailsSent() == 0 ? 0 :
                            Math.round((double) c.getCredentialsSubmitted() / c.getEmailsSent() * 100));
                    return t;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "totalCampaigns", totalCampaigns,
                "emailsSent", totalEmailsSent,
                "clickRate", clickRate,
                "submissionRate", submissionRate,
                "statusCounts", statusCounts,
                "atRiskUsers", atRisk,
                "recentActivity", recentActivity,
                "trend", trend
        ));
    }

    // ── Email Testing (Debug) ──────────────────────────────
    @PostMapping("/api/admin/email/test")
    public ResponseEntity<?> sendTestEmail(@AuthenticationPrincipal String email,
                                          @RequestBody TestEmailRequest request) {
        getAdminId(email); // Verify admin exists
        
        String subject = "PhishGuard Pro - Email Configuration Test";
        String htmlBody = """
            <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Email Configuration Test</h2>
                    <p>Hi,</p>
                    <p>This is a test email from PhishGuard Pro using your Google SMTP configuration.</p>
                    <p><strong>If you received this, your email setup is working correctly!</strong></p>
                    <hr>
                    <p>Test Details:</p>
                    <ul>
                        <li>Sent from: Google SMTP (smtp.gmail.com:587)</li>
                        <li>Test Time: """ + new java.util.Date() + """
                        <li>Admin Email: """ + email + """
                    </ul>
                    <p>You can now configure your campaigns to send phishing simulation emails.</p>
                    <br>
                    <p>Best regards,<br>PhishGuard Pro Team</p>
                </body>
            </html>
            """;
        
        try {
            emailService.sendTransactionalEmail(request.getRecipient(), subject, htmlBody);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Test email sent to " + request.getRecipient(),
                    "recipient", request.getRecipient()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // Request DTO for test email
    @lombok.Data
    public static class TestEmailRequest {
        private String recipient;
    }

    private Long getAdminId(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found")).getId();
    }
}
