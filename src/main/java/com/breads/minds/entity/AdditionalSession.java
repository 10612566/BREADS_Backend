package com.breads.minds.entity;

import com.breads.minds.entity.enums.AdditionalSessionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "additional_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdditionalSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private BeneficiaryReport report;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 30)
    private AdditionalSessionType sessionType;

    @Column(name = "sl_no")
    private Integer slNo;

    @Column(name = "session_date")
    private LocalDate date;

    @Column(length = 200)
    private String place;

    @Column(name = "school_name", length = 200)
    private String schoolName;

    @Builder.Default
    @Column(name = "participants_male")
    private Integer participantsMale = 0;

    @Builder.Default
    @Column(name = "participants_female")
    private Integer participantsFemale = 0;

    @Builder.Default
    @Column(name = "participants_total")
    private Integer participantsTotal = 0;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
