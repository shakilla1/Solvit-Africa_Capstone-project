package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_targets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampaignTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    private TargetUser targetUser;

    @Column(unique = true, nullable = false)
    private String trackingToken;

    @Builder.Default private boolean emailSent = false;
    @Builder.Default private boolean emailOpened = false;
    @Builder.Default private boolean linkClicked = false;
    @Builder.Default private boolean credentialsSubmitted = false;
    @Builder.Default private boolean trainingCompleted = false;

    private LocalDateTime emailSentAt;
    private LocalDateTime clickedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime scheduledSendAt;
}
