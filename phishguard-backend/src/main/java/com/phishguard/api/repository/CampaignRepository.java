package com.phishguard.api.repository;

import com.phishguard.api.entity.Campaign;
import com.phishguard.api.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByAdminId(Long adminId);
    List<Campaign> findByAdminIdAndStatus(Long adminId, CampaignStatus status);
    long countByAdminId(Long adminId);
}
