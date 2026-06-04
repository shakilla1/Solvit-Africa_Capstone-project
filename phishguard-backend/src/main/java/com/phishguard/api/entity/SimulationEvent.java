package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phishguard.api.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SimulationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "campaign_id")
    @JsonIgnoreProperties({"targets", "events", "admin", "template"})
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    private TargetUser targetUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String trackingToken;
    private String ipAddress;
    private String userAgent;
    private String capturedUsername;

    @JsonIgnore
    @Column(name = "captured_password_hash")
    private String capturedPasswordHash;

    private String capturedPasswordMasked;
    private String landingPageType;

    @CreationTimestamp
    private LocalDateTime occurredAt;
}
