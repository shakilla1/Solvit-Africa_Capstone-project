package com.phishguard.api.enums;

import lombok.Getter;

@Getter
public enum Plan {
    FREE(5, 3, false, false, false),
    STARTER(50, 20, true, true, false),
    PROFESSIONAL(500, 100, true, true, true),
    ENTERPRISE(Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, true);

    private final int maxUsers;
    private final int maxCampaigns;
    private final boolean educationEnabled;
    private final boolean analyticsEnabled;
    private final boolean customTemplatesEnabled;

    Plan(int maxUsers, int maxCampaigns, boolean educationEnabled,
         boolean analyticsEnabled, boolean customTemplatesEnabled) {
        this.maxUsers = maxUsers;
        this.maxCampaigns = maxCampaigns;
        this.educationEnabled = educationEnabled;
        this.analyticsEnabled = analyticsEnabled;
        this.customTemplatesEnabled = customTemplatesEnabled;
    }
}
