package com.phishguard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// ── Auth DTOs ──────────────────────────────────────────────

@Data
class AdminRegisterRequest {
    @NotBlank String fullName;
    @NotBlank String organisationName;
    @Email @NotBlank String email;
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password;
}

@Data
class AdminLoginRequest {
    @Email @NotBlank String email;
    @NotBlank String password;
}

@Data
class MfaVerifyRequest {
    @NotBlank String email;
    @NotBlank String totpCode;
}

@Data
class RefreshTokenRequest {
    @NotBlank String refreshToken;
}

@Data
class AuthResponse {
    String accessToken;
    String refreshToken;
    String email;
    String fullName;
    String role;
    String plan;
    boolean mfaRequired;
    String mfaSetupUri; // only on first login if MFA not yet set up
}

// ── Campaign DTOs ──────────────────────────────────────────

@Data
class CreateCampaignRequest {
    @NotBlank String name;
    String description;
    @NotBlank String difficulty;
    Long templateId;
    boolean autoRetest;
    int retestIntervalDays;
    // Targets: either CSV data or list of emails
    java.util.List<TargetDto> targets;
}

@Data
class TargetDto {
    String name;
    @Email String email;
    String department;
}

@Data
class CampaignResponse {
    Long id;
    String name;
    String description;
    String difficulty;
    String status;
    int totalTargets;
    int emailsSent;
    int linksClicked;
    int credentialsSubmitted;
    double clickRate;
    double submissionRate;
    String launchedAt;
    String createdAt;
}

// ── Notification DTOs ──────────────────────────────────────

@Data
class NotificationResponse {
    Long id;
    String category;
    String title;
    String message;
    String actionUrl;
    boolean read;
    String createdAt;
}

// ── Training DTOs ──────────────────────────────────────────

@Data
class SubmitQuizRequest {
    @NotBlank String topicId;
    java.util.Map<Integer, Integer> answers; // questionIndex -> answerIndex
}

@Data
class TrainingProgressResponse {
    String topicId;
    String topicTitle;
    String status;
    Integer score;
    Integer attempts;
    String completedAt;
    java.util.List<Integer> wrongQuestions;
}

// ── Simulation DTOs ────────────────────────────────────────

@Data
class TrackClickRequest {
    String token;
    String ipAddress;
    String userAgent;
}

@Data
class SubmitCredentialsRequest {
    @NotBlank String token;
    @NotBlank String username;
    @NotBlank String password;
    String landingPageType;
}

@Data
class CredentialCaptureResponse {
    String username;
    String passwordMasked;
    String landingType;
    String capturedAt;
    String ipAddress;
}

// ── Plan DTOs ──────────────────────────────────────────────

@Data
class PlanInfoResponse {
    String currentPlan;
    int maxUsers;
    int maxCampaigns;
    boolean educationEnabled;
    boolean analyticsEnabled;
    boolean customTemplatesEnabled;
    int usedUsers;
    int usedCampaigns;
    String planExpiresAt;
}
