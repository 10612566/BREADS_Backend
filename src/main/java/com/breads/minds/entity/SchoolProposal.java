package com.breads.minds.entity;

import com.breads.minds.entity.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "school_proposals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposed_by_user_id", nullable = false)
    private User proposedBy;

    @Column(name = "proposed_at", nullable = false)
    private LocalDateTime proposedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ProposalStatus status = ProposalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    // Location
    @Column(length = 100)
    private String taluka;

    @Column(name = "gram_panchayat", length = 100)
    private String gramPanchayat;

    @Column(name = "village_name", length = 100)
    private String villageName;

    @Column(name = "distance_from_center")
    private Double distanceFromCenter;

    // School info
    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "school_category", length = 50)
    private String schoolCategory;

    @Column(name = "school_status", length = 50)
    private String schoolStatus;

    @Column(name = "complete_address", length = 500)
    private String completeAddress;

    @Column(columnDefinition = "TEXT")
    private String justification;

    // Teacher counts
    @Column(name = "teachers_male")   private Integer teachersMale;
    @Column(name = "teachers_female") private Integer teachersFemale;
    @Column(name = "teachers_total")  private Integer teachersTotal;

    // Class enrollment
    @Column(name = "class5_boys")  private Integer class5Boys;
    @Column(name = "class5_girls") private Integer class5Girls;
    @Column(name = "class6_boys")  private Integer class6Boys;
    @Column(name = "class6_girls") private Integer class6Girls;
    @Column(name = "class7_boys")  private Integer class7Boys;
    @Column(name = "class7_girls") private Integer class7Girls;
    @Column(name = "class8_boys")  private Integer class8Boys;
    @Column(name = "class8_girls") private Integer class8Girls;
    @Column(name = "class9_boys")  private Integer class9Boys;
    @Column(name = "class9_girls") private Integer class9Girls;

    @Column(name = "grand_total_boys")  private Integer grandTotalBoys;
    @Column(name = "grand_total_girls") private Integer grandTotalGirls;
    @Column(name = "grand_total_total") private Integer grandTotalTotal;

    @Column(name = "groups_divided")    private Integer groupsDivided;
    @Column(name = "strength_per_group") private Integer strengthPerGroup;

    // Metrics
    @Column(name = "total_enrollment")               private Integer totalEnrollment;
    @Column(name = "marginalized_percentage")        private Double  marginalizedPercentage;
    @Column(name = "sdq_percentage")                 private Double  sdqPercentage;
    @Column(name = "academic_performance_percentage") private Double academicPerformancePercentage;
    @Column(name = "dropout_rate_percentage")        private Double  dropoutRatePercentage;
    @Column(name = "students_requiring_support")     private Integer studentsRequiringSupport;

    @Column(name = "has_school_counselor")    private Boolean hasSchoolCounselor;
    @Column(name = "school_counselor_name", length = 150) private String schoolCounselorName;

    @Column(name = "has_support_staff_substitution") private Boolean hasSupportStaffSubstitution;
    @Column(name = "has_professional_partnership")   private Boolean hasProfessionalPartnership;
    @Column(name = "teachers_willing_percentage")    private Double  teachersWillingPercentage;
    @Column(name = "proactive_administration")       private Boolean proactiveAdministration;
    @Column(name = "has_physical_space")             private Boolean hasPhysicalSpace;
    @Column(name = "has_basic_amenities")            private Boolean hasBasicAmenities;
    @Column(name = "amenities_list", length = 500)   private String  amenitiesList;  // comma-separated
    @Column(name = "is_high_risk_region")            private Boolean isHighRiskRegion;
    @Column(name = "has_active_sdmc")                private Boolean hasActiveSDMC;
    @Column(name = "has_staff_interest")             private Boolean hasStaffInterest;

    @Column(name = "overall_suitability", length = 10)
    private String overallSuitability;  // High | Medium | Low

    @Column(name = "selection_comments", columnDefinition = "TEXT")
    private String selectionComments;

    @Column(name = "estimated_beneficiaries")
    private Integer estimatedBeneficiaries;

    @PrePersist
    void onCreate() {
        if (proposedAt == null) proposedAt = LocalDateTime.now();
    }
}
