package com.phishguard.api.config;

import com.phishguard.api.enums.Plan;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PlanRequired {
    Plan value();
}
