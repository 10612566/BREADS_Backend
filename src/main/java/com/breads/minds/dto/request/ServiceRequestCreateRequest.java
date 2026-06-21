package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.ServiceRequestCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ServiceRequestCreateRequest {

    @NotNull private Long districtId;
    @NotNull private ServiceRequestCategory category;

    private String description;

    // Only populated when category = SCHOOL_SELECTION
    private SchoolProposalRequest schoolSelectionData;
}
