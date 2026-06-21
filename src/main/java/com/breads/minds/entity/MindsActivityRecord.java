package com.breads.minds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "minds_activity_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MindsActivityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "child_name", nullable = false, length = 150)
    private String childName;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "class_name", length = 20)
    private String className;

    @Column(name = "school_name", length = 200)
    private String schoolName;

    @Column(name = "intervention_type", length = 100)
    private String interventionType;

    @Column(length = 10)
    private String gender;  // Male | Female | Other

    @Column(length = 200)
    private String location;

    @Column(name = "topics_discussed", columnDefinition = "TEXT")
    private String topicsDiscussed;

    @Column(name = "session1_date")
    private LocalDate session1Date;

    @Column(name = "session2_date")
    private LocalDate session2Date;

    @Column(name = "session3_date")
    private LocalDate session3Date;

    @Column(columnDefinition = "TEXT")
    private String outcome;

    @Column(name = "follow_up", columnDefinition = "TEXT")
    private String followUp;

    @Column(columnDefinition = "TEXT")
    private String remarks;

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
