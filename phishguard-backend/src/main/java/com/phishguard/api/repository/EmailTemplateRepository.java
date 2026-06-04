package com.phishguard.api.repository;

import com.phishguard.api.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    List<EmailTemplate> findByIsDefaultTrue();
    List<EmailTemplate> findByAdminIdOrIsDefaultTrue(Long adminId);
}
