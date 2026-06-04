package com.phishguard.api.repository;

import com.phishguard.api.entity.CampaignTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampaignTargetRepository extends JpaRepository<CampaignTarget, Long> {
    Optional<CampaignTarget> findByTrackingToken(String token);
    List<CampaignTarget> findByCampaignId(Long campaignId);

    @Query("SELECT COUNT(ct) FROM CampaignTarget ct WHERE ct.targetUser.id = :userId AND ct.linkClicked = true")
    long countClicksByUserId(Long userId);

    @Query("SELECT COUNT(ct) FROM CampaignTarget ct WHERE ct.targetUser.id = :userId AND ct.credentialsSubmitted = true")
    long countSubmissionsByUserId(Long userId);

    @Query("SELECT COUNT(ct) FROM CampaignTarget ct WHERE ct.targetUser.id = :userId")
    long countCampaignsByUserId(Long userId);

    @Query("SELECT ct FROM CampaignTarget ct WHERE ct.emailSent = false AND (ct.scheduledSendAt <= :now OR ct.scheduledSendAt IS NULL)")
    List<CampaignTarget> findPendingSends(LocalDateTime now);
}
