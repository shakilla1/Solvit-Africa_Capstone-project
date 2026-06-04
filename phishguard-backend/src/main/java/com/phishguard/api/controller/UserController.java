package com.phishguard.api.controller;

import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.repository.SimulationEventRepository;
import com.phishguard.api.repository.TargetUserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final TargetUserRepository userRepo;
    private final SimulationEventRepository eventRepo;
    private final PasswordEncoder passwordEncoder;

    // ── Leaderboard ──────────────────────────────────────
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard(@AuthenticationPrincipal String email) {
        TargetUser me = getUser(email);
        List<TargetUser> all = userRepo.findAll(
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "totalPoints")
        );
        int[] rank = {1};
        var result = all.stream().map(u -> Map.of(
            "rank",    rank[0]++,
            "id",      u.getId(),
            "name",    u.getFullName(),
            "dept",    u.getDepartment() != null ? u.getDepartment() : "",
            "points",  u.getTotalPoints(),
            "badge",   u.getCurrentBadge() != null ? u.getCurrentBadge() : "Phishing Novice",
            "avatar",  initials(u.getFullName()),
            "current", u.getId().equals(me.getId())
        )).toList();
        return ResponseEntity.ok(result);
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        return parts.length >= 2
            ? String.valueOf(parts[0].charAt(0)) + parts[1].charAt(0)
            : String.valueOf(parts[0].charAt(0));
    }

    // ── Get all simulation events for the logged-in user ──
    @GetMapping("/events")
    public ResponseEntity<?> getMyEvents(@AuthenticationPrincipal String email) {
        TargetUser user = getUser(email);
        return ResponseEntity.ok(eventRepo.findByTargetUserIdOrderByOccurredAtDesc(user.getId()));
    }

    // ── Change password ────────────────────────────────────
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal String email,
                                             @RequestBody ChangePasswordRequest req) {
        TargetUser user = getUser(email);
        if (!passwordEncoder.matches(req.currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        if (req.newPassword == null || req.newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters.");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword));
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    private TargetUser getUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Data
    static class ChangePasswordRequest {
        String currentPassword;
        String newPassword;
    }
}
