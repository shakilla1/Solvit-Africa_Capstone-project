package com.phishguard.api.service.impl;

import com.phishguard.api.entity.AdminUser;
import com.phishguard.api.entity.Notification;
import com.phishguard.api.entity.TargetUser;
import com.phishguard.api.enums.NotificationCategory;
import com.phishguard.api.repository.AdminUserRepository;
import com.phishguard.api.repository.NotificationRepository;
import com.phishguard.api.repository.TargetUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notifRepo;
    private final AdminUserRepository adminRepo;
    private final TargetUserRepository userRepo;

    public void createAdminNotification(Long adminId, NotificationCategory category,
                                         String title, String message, String actionUrl) {
        AdminUser admin = adminRepo.findById(adminId).orElse(null);
        if (admin == null) return;
        notifRepo.save(Notification.builder()
                .admin(admin).category(category)
                .title(title).message(message).actionUrl(actionUrl).build());
    }

    public void createUserNotification(Long userId, NotificationCategory category,
                                        String title, String message, String actionUrl) {
        TargetUser user = userRepo.findById(userId).orElse(null);
        if (user == null) return;
        notifRepo.save(Notification.builder()
                .targetUser(user).category(category)
                .title(title).message(message).actionUrl(actionUrl).build());
    }

    public List<Notification> getAdminNotifications(Long adminId, String category) {
        if (category != null && !category.isBlank()) {
            return notifRepo.findByAdminIdAndCategoryOrderByCreatedAtDesc(
                    adminId, NotificationCategory.valueOf(category.toUpperCase()));
        }
        return notifRepo.findByAdminIdOrderByCreatedAtDesc(adminId);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notifRepo.findByTargetUserIdOrderByCreatedAtDesc(userId);
    }

    public Map<String, Long> getAdminUnreadCounts(Long adminId) {
        long campaign = notifRepo.findByAdminIdAndCategoryOrderByCreatedAtDesc(adminId, NotificationCategory.CAMPAIGN)
                .stream().filter(n -> !n.isRead()).count();
        long learning = notifRepo.findByAdminIdAndCategoryOrderByCreatedAtDesc(adminId, NotificationCategory.LEARNING)
                .stream().filter(n -> !n.isRead()).count();
        long billing  = notifRepo.findByAdminIdAndCategoryOrderByCreatedAtDesc(adminId, NotificationCategory.BILLING)
                .stream().filter(n -> !n.isRead()).count();
        return Map.of("campaign", campaign, "learning", learning,
                      "billing", billing, "total", campaign + learning + billing);
    }

    public void markAllReadAdmin(Long adminId)  { notifRepo.markAllReadForAdmin(adminId); }
    public void markAllReadUser(Long userId)    { notifRepo.markAllReadForUser(userId); }
    public void markOneRead(Long notifId) {
        notifRepo.findById(notifId).ifPresent(n -> { n.setRead(true); notifRepo.save(n); });
    }

    public void notifyPlanUpgrade(Long adminId, String oldPlan, String newPlan) {
        createAdminNotification(adminId, NotificationCategory.BILLING,
                "Plan Upgraded 🎉",
                "Your plan was upgraded from " + oldPlan + " to " + newPlan + ". New features are now available.",
                "/admin/subscription");
    }

    public void notifyFirstPayment(Long adminId, String plan) {
        createAdminNotification(adminId, NotificationCategory.BILLING,
                "Payment Received ✅",
                "Thank you for subscribing to the " + plan + " plan.",
                "/admin/subscription");
    }

    public void notifyPaymentFailed(Long adminId) {
        createAdminNotification(adminId, NotificationCategory.BILLING,
                "Payment Failed ⚠️",
                "Your last payment failed. Please update your payment method.",
                "/admin/subscription");
    }
}
