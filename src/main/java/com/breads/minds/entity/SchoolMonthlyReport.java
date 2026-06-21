package com.breads.minds.entity;

import com.breads.minds.entity.enums.VisitType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "school_monthly_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolMonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_visit", length = 20)
    private VisitType typeOfVisit;

    @Column(name = "minds_groups_established")
    private Integer mindsGroupsEstablished;

    @Column(name = "children_in_minds_groups")
    private Integer childrenInMindsGroups;

    @Column(name = "module_number")
    private Integer moduleNumber;

    @Column(name = "children_attended")
    private Integer childrenAttended;

    @Column(name = "action_prompts", columnDefinition = "TEXT")
    private String actionPrompts;

    @Column(name = "referred_for_counselling")
    private Integer referredForCounselling;

    @Column(name = "identified_with_issues")
    private Integer identifiedWithIssues;

    @Column(columnDefinition = "TEXT")
    private String challenges;

    @Column(name = "follow_up_plan", columnDefinition = "TEXT")
    private String followUpPlan;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "notes_for_next_module", columnDefinition = "TEXT")
    private String notesForNextModule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_user_id")
    private User submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @PrePersist
    void onCreate() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }
}
