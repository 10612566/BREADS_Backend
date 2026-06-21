package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.VisitType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SchoolMonthlyReportRequest {

    @NotNull private Long districtId;
    @NotNull private Long schoolId;
    @NotNull private LocalDate visitDate;
    private VisitType typeOfVisit;
    private Integer mindsGroupsEstablished;
    private Integer childrenInMindsGroups;
    private Integer moduleNumber;
    private Integer childrenAttended;
    private String  actionPrompts;
    private Integer referredForCounselling;
    private Integer identifiedWithIssues;
    private String  challenges;
    private String  followUpPlan;
    private LocalDate followUpDate;
    private String  notesForNextModule;
}
