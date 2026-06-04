package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "target_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TargetUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String department;

    @JsonIgnore
    private String password;

    @Builder.Default private int riskScore = 0;
    @Builder.Default private int totalPoints = 0;
    private String currentBadge;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private AdminUser admin;

    @JsonIgnore
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL)
    private List<SimulationEvent> events;

    @JsonIgnore
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL)
    private List<TrainingProgress> trainingProgress;

    @JsonIgnore
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
