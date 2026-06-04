package com.phishguard.api.service.impl;

import com.phishguard.api.entity.AdminUser;
import com.phishguard.api.entity.RefreshToken;
import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.enums.Plan;
import com.phishguard.api.enums.Role;
import com.phishguard.api.repository.AdminUserRepository;
import com.phishguard.api.repository.RefreshTokenRepository;
import com.phishguard.api.repository.TargetUserRepository;
import com.phishguard.api.security.JwtUtils;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminUserRepository adminRepo;
    private final TargetUserRepository targetUserRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${app.security.max-login-attempts}")
    private int maxLoginAttempts;

    @Value("${app.security.lockout-duration-minutes}")
    private int lockoutMinutes;

    // ── User (Target) Register ─────────────────────────────
    public Map<String, Object> userRegister(String fullName, String email, String rawPassword) {
        if (targetUserRepo.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists.");
        }
        TargetUser user = TargetUser.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        targetUserRepo.save(user);
        return userLogin(email, rawPassword);
    }

    // ── User (Target) Login ────────────────────────────────
    public Map<String, Object> userLogin(String email, String rawPassword) {
        TargetUser user = targetUserRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password.");
        }
        String accessToken  = jwtUtils.generateAccessToken(user.getEmail(), Role.ROLE_USER.name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());
        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .adminEmail(user.getEmail())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepo.save(rt);
        return Map.of(
                "accessToken",  accessToken,
                "refreshToken", refreshToken,
                "email",        user.getEmail(),
                "fullName",     user.getFullName(),
                "role",         Role.ROLE_USER.name(),
                "userId",       user.getId()
        );
    }

    // ── Register ───────────────────────────────────────────
    public AdminUser register(String fullName, String orgName, String email, String rawPassword) {
        if (adminRepo.existsByEmail(email)) {
            throw new RuntimeException("An account with this email already exists.");
        }
        AdminUser admin = AdminUser.builder()
                .fullName(fullName)
                .organisationName(orgName)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ROLE_ADMIN)
                .plan(Plan.ENTERPRISE)
                .build();
        return adminRepo.save(admin);
    }

    // ── Admin Login ────────────────────────────────────────
    public Map<String, Object> login(String email, String rawPassword) {
        AdminUser admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        if (admin.isLocked()) {
            throw new RuntimeException("Account is temporarily locked due to too many failed attempts. Try again in " + lockoutMinutes + " minutes.");
        }

        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
            admin.setFailedLoginAttempts(admin.getFailedLoginAttempts() + 1);
            if (admin.getFailedLoginAttempts() >= maxLoginAttempts) {
                admin.setAccountLocked(true);
                admin.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutMinutes));
            }
            adminRepo.save(admin);
            int remaining = maxLoginAttempts - admin.getFailedLoginAttempts();
            throw new RuntimeException("Invalid email or password." +
                    (remaining > 0 ? " " + remaining + " attempts remaining." : " Account locked."));
        }

        admin.setFailedLoginAttempts(0);
        admin.setAccountLocked(false);
        adminRepo.save(admin);

        Map<String, Object> result = new HashMap<>();

        if (admin.isMfaEnabled()) {
            result.put("mfaRequired", true);
            result.put("email", email);
            return result;
        }

        if (admin.getMfaSecret() == null) {
            String secret = new DefaultSecretGenerator().generate();
            admin.setMfaSecret(secret);
            adminRepo.save(admin);
            result.put("mfaSetupRequired", true);
            result.put("mfaSecret", secret);
            result.put("mfaQrUri", generateQrDataUri(email, secret));
        }

        return issueTokens(admin, result);
    }

    // ── MFA Enable ─────────────────────────────────────────
    public Map<String, Object> enableMfa(String email, String totpCode) {
        AdminUser admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!verifyTotp(admin.getMfaSecret(), totpCode)) {
            throw new RuntimeException("Invalid verification code. Please try again.");
        }
        admin.setMfaEnabled(true);
        adminRepo.save(admin);
        Map<String, Object> result = new HashMap<>();
        result.put("mfaEnabled", true);
        return issueTokens(admin, result);
    }

    // ── MFA Verify ─────────────────────────────────────────
    public Map<String, Object> verifyMfa(String email, String totpCode) {
        AdminUser admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (!verifyTotp(admin.getMfaSecret(), totpCode)) {
            throw new RuntimeException("Invalid verification code.");
        }
        return issueTokens(admin, new HashMap<>());
    }

    // ── Refresh Token ──────────────────────────────────────
    public Map<String, Object> refreshToken(String refreshTokenStr) {
        RefreshToken stored = refreshTokenRepo.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired or revoked. Please log in again.");
        }
        AdminUser admin = adminRepo.findByEmail(stored.getAdminEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        stored.setRevoked(true);
        refreshTokenRepo.save(stored);
        return issueTokens(admin, new HashMap<>());
    }

    // ── Logout ─────────────────────────────────────────────
    public void logout(String email) {
        refreshTokenRepo.revokeAllForAdmin(email);
    }

    // ── Helpers ────────────────────────────────────────────
    private Map<String, Object> issueTokens(AdminUser admin, Map<String, Object> extra) {
        String accessToken  = jwtUtils.generateAccessToken(admin.getEmail(), admin.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(admin.getEmail());
        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .adminEmail(admin.getEmail())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepo.save(rt);
        extra.put("accessToken",  accessToken);
        extra.put("refreshToken", refreshToken);
        extra.put("email",        admin.getEmail());
        extra.put("fullName",     admin.getFullName());
        extra.put("role",         admin.getRole().name());
        extra.put("plan",         admin.getPlan().name());
        extra.put("mfaRequired",  false);
        return extra;
    }

    private boolean verifyTotp(String secret, String code) {
        CodeGenerator codeGen = new DefaultCodeGenerator();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGen, new SystemTimeProvider());
        return verifier.isValidCode(secret, code);
    }

    private String generateQrDataUri(String email, String secret) {
        try {
            QrData data = new QrData.Builder()
                    .label(email)
                    .secret(secret)
                    .issuer("PhishGuard Pro")
                    .build();
            byte[] imageData = new ZxingPngQrGenerator().generate(data);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageData);
        } catch (Exception e) {
            return "";
        }
    }
}
