package com.phishguard.api.controller;

import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.repository.CampaignTargetRepository;
import com.phishguard.api.repository.TargetUserRepository;
import com.phishguard.api.service.impl.TrainingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/training")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final TargetUserRepository userRepo;
    private final CampaignTargetRepository targetRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal String email) {
        TargetUser user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        var progress = trainingService.getUserProgress(user.getId());
        return ResponseEntity.ok(Map.of(
                "id",           user.getId(),
                "fullName",     user.getFullName(),
                "email",        user.getEmail(),
                "department",   user.getDepartment() != null ? user.getDepartment() : "",
                "riskScore",    user.getRiskScore(),
                "totalPoints",  user.getTotalPoints(),
                "currentBadge", user.getCurrentBadge() != null ? user.getCurrentBadge() : "",
                "progress",     progress
        ));
    }

    @GetMapping("/progress")
    public ResponseEntity<?> getProgress(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(trainingService.getUserProgress(getUserId(email)));
    }

    @PostMapping("/topics/{topicId}/start")
    public ResponseEntity<?> startTopic(@PathVariable String topicId,
                                         @RequestBody Map<String, String> body,
                                         @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(trainingService.startTopic(getUserId(email), topicId, body.get("topicTitle")));
    }

    @PostMapping("/topics/{topicId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable String topicId,
                                         @RequestBody QuizSubmitRequest req,
                                         @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(trainingService.submitQuiz(getUserId(email), topicId, req.answers));
    }

    // ── Token-based endpoints (no login required) ──────────
    @PostMapping("/token/{token}/start")
    public ResponseEntity<?> startByToken(@PathVariable String token,
                                           @RequestBody Map<String, String> body) {
        var ct = targetRepo.findByTrackingToken(token);
        if (ct.isEmpty()) return ResponseEntity.ok(Map.of("status", "token_not_found"));
        Long userId = ct.get().getTargetUser().getId();
        return ResponseEntity.ok(trainingService.startTopic(userId, body.get("topicId"), body.get("topicTitle")));
    }

    @PostMapping("/token/{token}/submit")
    public ResponseEntity<?> submitByToken(@PathVariable String token,
                                            @RequestBody QuizSubmitRequest req) {
        var ct = targetRepo.findByTrackingToken(token);
        if (ct.isEmpty()) return ResponseEntity.ok(Map.of("status", "token_not_found", "score", 0));
        Long userId = ct.get().getTargetUser().getId();
        return ResponseEntity.ok(trainingService.submitQuiz(userId, req.topicId, req.answers));
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordRequest req) {
        TargetUser user = userRepo.findByEmail(req.email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(req.password));
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Password set successfully"));
    }

    private Long getUserId(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
    }

    @Data static class QuizSubmitRequest {
        String topicId;
        java.util.Map<Integer, Integer> answers;
    }

    @Data static class SetPasswordRequest {
        String email;
        String password;
    }
}
