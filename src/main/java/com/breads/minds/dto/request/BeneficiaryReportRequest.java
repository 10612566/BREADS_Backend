package com.breads.minds.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BeneficiaryReportRequest {

    @NotNull private Long districtId;

    @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}", message = "Month must be in YYYY-MM format")
    private String month;

    @NotNull private Integer year;

    @Min(0) private Integer childrenReached      = 0;
    @Min(0) private Integer parentsReached       = 0;
    @Min(0) private Integer professionalsReached = 0;
    @Min(0) private Integer teachersReached      = 0;
    @Min(0) private Integer volunteersReached    = 0;

    @Min(0) private Integer modules            = 0;
    @Min(0) private Integer communityAwareness = 0;
    @Min(0) private Integer artTherapy         = 0;
    @Min(0) private Integer counselling        = 0;

    private String narrativeImpact;
}
