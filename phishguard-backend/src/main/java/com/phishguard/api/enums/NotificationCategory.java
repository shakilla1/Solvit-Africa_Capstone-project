package com.phishguard.api.enums;

public enum NotificationCategory {
    CAMPAIGN,   // click detected, credentials submitted, campaign launched
    LEARNING,   // training completed, quiz failed, badge earned
    BILLING     // payment received, plan upgraded, payment failed
}
