package com.phishguard.api.repository;

import com.phishguard.api.entity.Notification;
import com.phishguard.api.enums.NotificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    List<Notification> findByAdminIdAndCategoryOrderByCreatedAtDesc(Long adminId, NotificationCategory category);
    List<Notification> findByTargetUserIdOrderByCreatedAtDesc(Long userId);
    long countByAdminIdAndReadFalse(Long adminId);
    long countByTargetUserIdAndReadFalse(Long userId);

    @Modifying @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.admin.id = :adminId")
    void markAllReadForAdmin(Long adminId);

    @Modifying @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.targetUser.id = :userId")
    void markAllReadForUser(Long userId);
}
