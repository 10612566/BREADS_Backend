package com.breads.minds.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {

    // Beneficiary totals (current year)
    private long totalChildrenReached;
    private long totalParentsReached;
    private long totalProfessionalsReached;
    private long totalTeachersReached;
    private long totalVolunteersReached;
    private long grandTotalBeneficiaries;

    // Annual targets
    private int targetChildren;
    private int targetParents;
    private int targetProfessionals;
    private int targetTeachers;
    private int targetVolunteers;

    // Counts
    private long totalSchools;
    private long activeSchools;
    private long totalDistricts;
    private long totalUsers;
    private long pendingServiceRequests;
    private long pendingUserApprovals;
    private long mindsActivityRecords;

    // Per-district breakdown
    private Map<String, Long> beneficiariesByDistrict;

    // Monthly trend (last 12 months)
    private Map<String, Long> monthlyBeneficiaryTrend;

    // Report submission status for current month
    private long reportsSubmitted;
    private long reportsExpected;
}
