package com.phishguard.api.controller;

import com.phishguard.api.entity.SimulationEvent;
import com.phishguard.api.entity.CampaignTarget;
import com.phishguard.api.enums.EventType;
import com.phishguard.api.repository.CampaignTargetRepository;
import com.phishguard.api.repository.CampaignRepository;
import com.phishguard.api.repository.SimulationEventRepository;
import com.phishguard.api.service.impl.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final CampaignService campaignService;
    private final CampaignTargetRepository targetRepo;
    private final CampaignRepository campaignRepo;
    private final SimulationEventRepository eventRepo;

    /** 1x1 pixel open tracker — embedded in email */
    @GetMapping("/api/open/{token}")
    public ResponseEntity<byte[]> trackOpen(@PathVariable String token,
                                             HttpServletRequest request) {
        targetRepo.findByTrackingToken(token).ifPresent(ct -> {
            if (!ct.isEmailOpened()) {
                ct.setEmailOpened(true);
                targetRepo.save(ct);
                var c = ct.getCampaign();
                c.setEmailsOpened(c.getEmailsOpened() + 1);
                campaignRepo.save(c);
                eventRepo.save(SimulationEvent.builder()
                        .campaign(c).targetUser(ct.getTargetUser())
                        .eventType(EventType.EMAIL_OPENED)
                        .trackingToken(token)
                        .ipAddress(getClientIp(request))
                        .userAgent(request.getHeader("User-Agent"))
                        .build());
            }
        });
        // Return 1x1 transparent GIF
        byte[] gif = {71,73,70,56,57,97,1,0,1,0,-128,0,0,-1,-1,-1,0,0,0,33,-7,4,0,0,0,0,0,44,0,0,0,0,1,0,1,0,0,2,2,68,1,0,59};
        return ResponseEntity.ok().header("Content-Type","image/gif").body(gif);
    }

    /** Called when target clicks the phishing link */
    @GetMapping("/api/track/{token}")
    public ResponseEntity<?> trackClick(@PathVariable String token,
                                         HttpServletRequest request) {
        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");
        Map<String, Object> result = campaignService.recordClick(token, ip, ua);
        return ResponseEntity.ok(result);
    }

    /** Called when target submits credentials on fake landing page */
    @PostMapping("/api/sim/{token}/submit")
    public ResponseEntity<?> submitCredentials(@PathVariable String token,
                                                @RequestBody CredSubmitRequest req,
                                                HttpServletRequest request) {
        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");
        Map<String, Object> result = campaignService.recordCredentials(
                token, req.username, req.password, req.landingType, ip, ua);
        return ResponseEntity.ok(result);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    @Data
    static class CredSubmitRequest {
        String username;
        String password;
        String landingType;
    }
}
