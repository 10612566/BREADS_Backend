package com.breads.minds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "beneficiary_reports",
       uniqueConstraints = @UniqueConstraint(columnNames = {"district_id", "month"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BeneficiaryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false, length = 7)
    private String month;     // YYYY-MM

    @Column(nullable = false)
    private Integer year;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    // Targets reached per group
    @Builder.Default @Column(name = "children_reached")      private Integer childrenReached      = 0;
    @Builder.Default @Column(name = "parents_reached")       private Integer parentsReached       = 0;
    @Builder.Default @Column(name = "professionals_reached") private Integer professionalsReached = 0;
    @Builder.Default @Column(name = "teachers_reached")      private Integer teachersReached      = 0;
    @Builder.Default @Column(name = "volunteers_reached")    private Integer volunteersReached    = 0;

    // Simple activity counts
    @Builder.Default @Column(name = "modules")             private Integer modules           = 0;
    @Builder.Default @Column(name = "community_awareness") private Integer communityAwareness = 0;
    @Builder.Default @Column(name = "art_therapy")         private Integer artTherapy         = 0;
    @Builder.Default @Column(name = "counselling")         private Integer counselling         = 0;

    // Detailed training sessions (Teacher, Parent, Volunteer, Practitioner)
    @Builder.Default
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainingSession> trainingSessions = new ArrayList<>();

    // Additional sessions (extra classes, additional counselling beyond MINDS club)
    @Builder.Default
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdditionalSession> additionalSessions = new ArrayList<>();

    @Column(name = "narrative_impact", columnDefinition = "TEXT")
    private String narrativeImpact;

    @Builder.Default
    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @PrePersist
    void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }
}
