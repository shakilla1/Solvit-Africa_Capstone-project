package com.phishguard.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.entity.TrainingProgress;
import com.phishguard.api.enums.NotificationCategory;
import com.phishguard.api.repository.TargetUserRepository;
import com.phishguard.api.repository.TrainingProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {

    private final TrainingProgressRepository progressRepo;
    private final TargetUserRepository userRepo;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Start a topic ──────────────────────────────────────
    @Transactional
    public TrainingProgress startTopic(Long userId, String topicId, String topicTitle) {
        return progressRepo.findByTargetUserIdAndTopicId(userId, topicId)
                .orElseGet(() -> {
                    TargetUser user = userRepo.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    TrainingProgress p = TrainingProgress.builder()
                            .targetUser(user)
                            .topicId(topicId)
                            .topicTitle(topicTitle)
                            .status("IN_PROGRESS")
                            .attempts(0)
                            .build();
                    return progressRepo.save(p);
                });
    }

    // ── Submit quiz answers ────────────────────────────────
    @Transactional
    public Map<String, Object> submitQuiz(Long userId, String topicId,
                                           Map<Integer, Integer> answers) {
        TrainingProgress progress = progressRepo.findByTargetUserIdAndTopicId(userId, topicId)
                .orElseThrow(() -> new RuntimeException("Topic not started"));

        // Get correct answers for this topic
        Map<Integer, Integer> correctAnswers = getCorrectAnswers(topicId);
        List<Integer> wrongQuestions = new ArrayList<>();
        int correct = 0;

        for (Map.Entry<Integer, Integer> entry : answers.entrySet()) {
            Integer qIdx = entry.getKey();
            Integer given = entry.getValue();
            Integer expected = correctAnswers.get(qIdx);
            if (expected != null && expected.equals(given)) {
                correct++;
            } else {
                wrongQuestions.add(qIdx);
            }
        }

        int total = correctAnswers.size();
        int score = total > 0 ? (int) Math.round((double) correct / total * 100) : 0;

        // Save progress
        progress.setScore(score);
        progress.setAttempts(progress.getAttempts() + 1);
        progress.setStatus("COMPLETED");
        progress.setCompletedAt(LocalDateTime.now());

        try {
            progress.setAnswersJson(objectMapper.writeValueAsString(answers));
            progress.setWrongQuestionsJson(objectMapper.writeValueAsString(wrongQuestions));
        } catch (Exception ignored) {}

        progressRepo.save(progress);

        // Update user points
        TargetUser user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTotalPoints(user.getTotalPoints() + score);

        // Reduce risk score on training completion
        user.setRiskScore(Math.max(0, user.getRiskScore() - 10));

        // Check if all topics completed → award badge
        long completedCount = progressRepo.findByTargetUserId(userId)
                .stream().filter(p -> "COMPLETED".equals(p.getStatus())).count();

        String badge = null;
        if (completedCount >= 5) {
            badge = "Phishing Detective";
            user.setCurrentBadge(badge);
            notificationService.createUserNotification(userId, NotificationCategory.LEARNING,
                    "Badge Earned! 🏆",
                    "You earned the \"Phishing Detective\" badge for completing all training topics!",
                    "/badges");
            // Notify admin too
            if (user.getAdmin() != null) {
                notificationService.createAdminNotification(user.getAdmin().getId(),
                        NotificationCategory.LEARNING,
                        "Training Completed",
                        user.getFullName() + " completed all training topics and earned the Phishing Detective badge.",
                        "/admin/education");
            }
        } else if (score >= 70) {
            badge = "Security Aware";
            if (user.getCurrentBadge() == null) user.setCurrentBadge(badge);
            notificationService.createUserNotification(userId, NotificationCategory.LEARNING,
                    "Topic Completed ✅",
                    "You scored " + score + "% on \"" + progress.getTopicTitle() + "\". Keep going!",
                    "/training");
        } else {
            // Failed — notify admin so they can help
            if (user.getAdmin() != null) {
                notificationService.createAdminNotification(user.getAdmin().getId(),
                        NotificationCategory.LEARNING,
                        "Learner Needs Help",
                        user.getFullName() + " scored " + score + "% on \"" + progress.getTopicTitle() +
                                "\". They may need additional support.",
                        "/admin/education");
            }
            notificationService.createUserNotification(userId, NotificationCategory.LEARNING,
                    "Keep Practising 💪",
                    "You scored " + score + "% on \"" + progress.getTopicTitle() + "\". Review the material and try again.",
                    "/training");
        }

        userRepo.save(user);

        return Map.of(
                "score", score,
                "correct", correct,
                "total", total,
                "wrongQuestions", wrongQuestions,
                "badge", badge != null ? badge : "",
                "passed", score >= 70
        );
    }

    // ── Get all progress for a user ────────────────────────
    public List<TrainingProgress> getUserProgress(Long userId) {
        return progressRepo.findByTargetUserId(userId);
    }

    // ── Get all progress for admin monitoring ──────────────
    public List<TrainingProgress> getProgressForAdmin(Long adminId) {
        return progressRepo.findByTargetUserAdminId(adminId);
    }

    // ── Correct answers per topic ──────────────────────────
    private Map<Integer, Integer> getCorrectAnswers(String topicId) {
        return switch (topicId) {
            case "email-red-flags"    -> Map.of(0, 1, 1, 2, 2, 0, 3, 1);
            case "suspicious-links"   -> Map.of(0, 1, 1, 0, 2, 2, 3, 1);
            case "domain-spoofing"    -> Map.of(0, 1, 1, 2, 2, 0, 3, 3);
            case "social-engineering" -> Map.of(0, 2, 1, 1, 2, 0, 3, 2);
            case "credential-safety"  -> Map.of(0, 0, 1, 3, 2, 1, 3, 2);
            default -> Map.of();
        };
    }
}
