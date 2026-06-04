package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"target_user_id", "topic_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TrainingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id")
    @JsonIgnoreProperties({"events", "trainingProgress", "notifications", "password"})
    private TargetUser targetUser;

    @Column(nullable = false)
    private String topicId;

    @Column(nullable = false)
    private String topicTitle;

    @Column(nullable = false)
    private String status = "NOT_STARTED";

    private Integer score;
    private Integer attempts = 0;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String answersJson;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    private String wrongQuestionsJson;

    @CreationTimestamp
    private LocalDateTime startedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;
}
