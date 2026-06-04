package com.phishguard.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phishguard.api.enums.Plan;
import com.phishguard.api.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "admin_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String organisationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_ADMIN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plan plan = Plan.ENTERPRISE;

    private boolean mfaEnabled = false;
    @JsonIgnore
    private String mfaSecret;

    private int failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;
    private boolean accountLocked = false;

    private String stripeCustomerId;
    private LocalDateTime planExpiresAt;
    private LocalDateTime planActivatedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Campaign> campaigns;

    @JsonIgnore
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    public boolean isLocked() {
        if (!accountLocked) return false;
        if (lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            accountLocked = false;
            failedLoginAttempts = 0;
            return false;
        }
        return true;
    }
}
