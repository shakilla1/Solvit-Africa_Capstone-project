package com.phishguard.api.repository;

import com.phishguard.api.entity.TargetUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TargetUserRepository extends JpaRepository<TargetUser, Long> {
    Optional<TargetUser> findByEmail(String email);
    List<TargetUser> findByAdminId(Long adminId);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM TargetUser u WHERE u.admin.id = :adminId ORDER BY u.riskScore DESC")
    List<TargetUser> findTopAtRiskByAdminId(Long adminId);
}
