package com.phishguard.api.controller;

import com.phishguard.api.entity.Campaign;
import com.phishguard.api.repository.AdminUserRepository;
import com.phishguard.api.repository.CampaignTargetRepository;
import com.phishguard.api.service.impl.CampaignService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {

    private final CampaignService campaignService;
    private final AdminUserRepository adminRepo;
    private final CampaignTargetRepository targetRepo;

    @GetMapping
    public ResponseEntity<List<Campaign>> list(@AuthenticationPrincipal String email) {
        Long adminId = getAdminId(email);
        return ResponseEntity.ok(campaignService.getCampaigns(adminId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> get(@PathVariable Long id,
                                         @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(campaignService.getCampaign(id, getAdminId(email)));
    }

    @PostMapping
    public ResponseEntity<Campaign> create(@RequestBody CreateRequest req,
                                            @AuthenticationPrincipal String email) {
        Campaign c = campaignService.createCampaign(
                getAdminId(email), req.name, req.description, req.difficulty,
                req.templateId, req.autoRetest, req.retestIntervalDays, req.targets);
        return ResponseEntity.ok(c);
    }

    @PostMapping("/{id}/launch")
    public ResponseEntity<Campaign> launch(@PathVariable Long id,
                                            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(campaignService.launchCampaign(id, getAdminId(email)));
    }

    @PostMapping("/{id}/toggle-pause")
    public ResponseEntity<Campaign> togglePause(@PathVariable Long id,
                                                 @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(campaignService.togglePause(id, getAdminId(email)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                     @AuthenticationPrincipal String email) {
        campaignService.deleteCampaign(id, getAdminId(email));
        return ResponseEntity.ok(Map.of("message", "Campaign deleted"));
    }

    @GetMapping("/{id}/targets")
    public ResponseEntity<?> getTargets(@PathVariable Long id,
                                         @AuthenticationPrincipal String email) {
        campaignService.getCampaign(id, getAdminId(email)); // verify ownership
        return ResponseEntity.ok(targetRepo.findByCampaignId(id));
    }

    @GetMapping("/{id}/report/csv")
    public ResponseEntity<byte[]> downloadCsv(@PathVariable Long id,
                                               @AuthenticationPrincipal String email) {
        Campaign c = campaignService.getCampaign(id, getAdminId(email));
        StringBuilder csv = new StringBuilder();
        csv.append("Name,Email,Department,Email Sent,Link Clicked,Credentials Submitted,Clicked At,Submitted At\n");
        c.getTargets().forEach(t -> csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                t.getTargetUser().getFullName(),
                t.getTargetUser().getEmail(),
                t.getTargetUser().getDepartment(),
                t.isEmailSent(),
                t.isLinkClicked(),
                t.isCredentialsSubmitted(),
                t.getClickedAt() != null ? t.getClickedAt() : "",
                t.getSubmittedAt() != null ? t.getSubmittedAt() : ""
        )));
        byte[] bytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String filename = "campaign-" + id + "-report.csv";
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    private Long getAdminId(String email) {
        return adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found")).getId();
    }

    @Data
    static class CreateRequest {
        String name;
        String description;
        String difficulty;
        Long templateId;
        boolean autoRetest;
        int retestIntervalDays;
        List<Map<String, String>> targets;
    }
}
