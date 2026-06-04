package com.phishguard.api.repository;

import com.phishguard.api.entity.TrainingProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrainingProgressRepository extends JpaRepository<TrainingProgress, Long> {
    Optional<TrainingProgress> findByTargetUserIdAndTopicId(Long userId, String topicId);
    List<TrainingProgress> findByTargetUserId(Long userId);
    List<TrainingProgress> findByTargetUserAdminId(Long adminId);
}
