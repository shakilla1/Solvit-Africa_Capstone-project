package com.phishguard.api.controller;

import com.phishguard.api.repository.AdminUserRepository;
import com.phishguard.api.repository.TargetUserRepository;
import com.phishguard.api.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notifService;
    private final AdminUserRepository adminRepo;
    private final TargetUserRepository userRepo;

    // ── Admin notifications ────────────────────────────────
    @GetMapping("/api/admin/notifications")
    public ResponseEntity<?> adminNotifications(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) String category) {
        Long adminId = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found")).getId();
        return ResponseEntity.ok(notifService.getAdminNotifications(adminId, category));
    }

    @GetMapping("/api/admin/notifications/counts")
    public ResponseEntity<?> adminCounts(@AuthenticationPrincipal String email) {
        Long adminId = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found")).getId();
        return ResponseEntity.ok(notifService.getAdminUnreadCounts(adminId));
    }

    @PostMapping("/api/admin/notifications/mark-all-read")
    public ResponseEntity<?> adminMarkAllRead(@AuthenticationPrincipal String email) {
        Long adminId = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found")).getId();
        notifService.markAllReadAdmin(adminId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @PostMapping("/api/admin/notifications/{id}/read")
    public ResponseEntity<?> adminMarkOneRead(@PathVariable Long id) {
        notifService.markOneRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }

    // ── User notifications ─────────────────────────────────
    @GetMapping("/api/user/notifications")
    public ResponseEntity<?> userNotifications(@AuthenticationPrincipal String email) {
        Long userId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        return ResponseEntity.ok(notifService.getUserNotifications(userId));
    }

    @PostMapping("/api/user/notifications/mark-all-read")
    public ResponseEntity<?> userMarkAllRead(@AuthenticationPrincipal String email) {
        Long userId = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")).getId();
        notifService.markAllReadUser(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }
}
