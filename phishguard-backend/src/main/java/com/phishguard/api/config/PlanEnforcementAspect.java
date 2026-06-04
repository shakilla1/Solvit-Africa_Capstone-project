package com.phishguard.api.config;

import com.phishguard.api.entity.AdminUser;
import com.phishguard.api.enums.Plan;
import com.phishguard.api.exception.PlanUpgradeRequiredException;
import com.phishguard.api.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PlanEnforcementAspect {

    private final AdminUserRepository adminUserRepository;

    @Before("@annotation(planRequired)")
    public void checkPlan(PlanRequired planRequired) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AdminUser admin = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Plan required = planRequired.value();
        Plan current  = admin.getPlan();

        if (current.ordinal() < required.ordinal()) {
            throw new PlanUpgradeRequiredException(
                "This feature requires the " + required.name() + " plan or above. " +
                "Your current plan is " + current.name() + "."
            );
        }
    }
}
