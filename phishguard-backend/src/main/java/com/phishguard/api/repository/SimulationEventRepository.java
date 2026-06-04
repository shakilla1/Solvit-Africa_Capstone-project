package com.phishguard.api.repository;

import com.phishguard.api.entity.SimulationEvent;
import com.phishguard.api.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SimulationEventRepository extends JpaRepository<SimulationEvent, Long> {
    List<SimulationEvent> findByCampaignIdOrderByOccurredAtDesc(Long campaignId);
    List<SimulationEvent> findByTargetUserIdOrderByOccurredAtDesc(Long userId);
    List<SimulationEvent> findByCampaignAdminIdOrderByOccurredAtDesc(Long adminId);
    long countByCampaignIdAndEventType(Long campaignId, EventType type);
}
