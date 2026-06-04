package com.phishguard.api.controller;

import com.phishguard.api.service.impl.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req.email, req.password));
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> userRegister(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.userRegister(req.fullName, req.email, req.password));
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.userLogin(req.email, req.password));
    }

    @PostMapping("/admin/mfa/enable")
    public ResponseEntity<?> enableMfa(@RequestBody MfaRequest req) {
        return ResponseEntity.ok(authService.enableMfa(req.email, req.code));
    }

    @PostMapping("/admin/mfa/verify")
    public ResponseEntity<?> verifyMfa(@RequestBody MfaRequest req) {
        return ResponseEntity.ok(authService.verifyMfa(req.email, req.code));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authService.refreshToken(body.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal String email) {
        authService.logout(email);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ── Inner request classes ──────────────────────────────
    @Data static class LoginRequest {
        @Email @NotBlank String email;
        @NotBlank String password;
    }

    @Data static class RegisterRequest {
        @NotBlank String fullName;
        @Email @NotBlank String email;
        @NotBlank String password;
    }

    @Data static class MfaRequest {
        @NotBlank String email;
        @NotBlank String code;
    }
}
