package com.breads.minds.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SchoolProposalRequest {

    @NotBlank private String schoolName;
    private String taluka;
    private String gramPanchayat;
    private String villageName;
    private Double distanceFromCenter;
    private String schoolCategory;
    private String schoolStatus;
    private String completeAddress;
    private String justification;

    private Integer teachersMale;
    private Integer teachersFemale;
    private Integer teachersTotal;

    private Integer class5Boys;  private Integer class5Girls;
    private Integer class6Boys;  private Integer class6Girls;
    private Integer class7Boys;  private Integer class7Girls;
    private Integer class8Boys;  private Integer class8Girls;
    private Integer class9Boys;  private Integer class9Girls;

    private Integer grandTotalBoys;
    private Integer grandTotalGirls;
    private Integer grandTotalTotal;
    private Integer groupsDivided;
    private Integer strengthPerGroup;

    private Integer totalEnrollment;
    private Double  marginalizedPercentage;
    private Double  sdqPercentage;
    private Double  academicPerformancePercentage;
    private Double  dropoutRatePercentage;
    private Integer studentsRequiringSupport;

    private Boolean hasSchoolCounselor;
    private String  schoolCounselorName;
    private Boolean hasSupportStaffSubstitution;
    private Boolean hasProfessionalPartnership;
    private Double  teachersWillingPercentage;
    private Boolean proactiveAdministration;
    private Boolean hasPhysicalSpace;
    private Boolean hasBasicAmenities;
    private List<String> amenitiesList;
    private Boolean isHighRiskRegion;
    private Boolean hasActiveSDMC;
    private Boolean hasStaffInterest;
    private String  overallSuitability;
    private String  selectionComments;
    private Integer estimatedBeneficiaries;
}
