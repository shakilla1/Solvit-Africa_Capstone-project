package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phishguard.api.enums.CampaignStatus;
import com.phishguard.api.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminUser admin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id")
    private EmailTemplate template;

    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;
    private int sendWindowHours = 48;

    private boolean autoRetest = false;
    private int retestIntervalDays = 14;
    private LocalDateTime nextRetestAt;

    private int totalTargets = 0;
    private int emailsSent = 0;
    private int emailsOpened = 0;
    private int linksClicked = 0;
    private int credentialsSubmitted = 0;
    private int trainingsCompleted = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<CampaignTarget> targets;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL)
    private List<SimulationEvent> events;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime launchedAt;
    private LocalDateTime completedAt;

    public double getClickRate() {
        return emailsSent == 0 ? 0 : (double) linksClicked / emailsSent * 100;
    }

    public double getSubmissionRate() {
        return emailsSent == 0 ? 0 : (double) credentialsSubmitted / emailsSent * 100;
    }
}
