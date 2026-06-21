package com.breads.minds.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schools")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(length = 100)
    private String taluka;

    @Column(name = "gram_panchayat", length = 100)
    private String gramPanchayat;

    @Column(length = 100)
    private String village;

    @Column(name = "distance_from_center")
    private Double distanceFromCenter;

    @Column(name = "school_category", length = 50)
    private String schoolCategory;

    @Column(name = "school_status", length = 50)
    private String schoolStatus;

    @Column(name = "complete_address", length = 500)
    private String completeAddress;

    // Teacher counts
    @Column(name = "teachers_male")
    private Integer teachersMale;

    @Column(name = "teachers_female")
    private Integer teachersFemale;

    @Column(name = "teachers_total")
    private Integer teachersTotal;

    // Class-wise enrollment
    @Column(name = "class6_boys")  private Integer class6Boys;
    @Column(name = "class6_girls") private Integer class6Girls;
    @Column(name = "class7_boys")  private Integer class7Boys;
    @Column(name = "class7_girls") private Integer class7Girls;
    @Column(name = "class8_boys")  private Integer class8Boys;
    @Column(name = "class8_girls") private Integer class8Girls;
    @Column(name = "class9_boys")  private Integer class9Boys;
    @Column(name = "class9_girls") private Integer class9Girls;

    @Column(name = "grand_total_total")
    private Integer grandTotalTotal;

    @Column(name = "groups_divided")
    private Integer groupsDivided;

    @Column(name = "strength_per_group")
    private Integer strengthPerGroup;

    // Metrics
    @Column(name = "total_enrollment")
    private Integer totalEnrollment;

    @Column(name = "marginalized_percentage")
    private Double marginalizedPercentage;

    @Column(name = "sdq_percentage")
    private Double sdqPercentage;

    @Column(name = "academic_performance_percentage")
    private Double academicPerformancePercentage;

    @Column(name = "dropout_rate_percentage")
    private Double dropoutRatePercentage;

    @Column(name = "students_requiring_support")
    private Integer studentsRequiringSupport;

    @Column(name = "has_school_counselor")
    private Boolean hasSchoolCounselor;

    @Column(name = "school_counselor_name", length = 150)
    private String schoolCounselorName;

    @Column(name = "has_basic_amenities")
    private Boolean hasBasicAmenities;

    @Column(name = "amenities_list", length = 500)
    private String amenitiesList;  // comma-separated

    @Column(name = "overall_suitability", length = 10)
    private String overallSuitability;  // High | Medium | Low
}
