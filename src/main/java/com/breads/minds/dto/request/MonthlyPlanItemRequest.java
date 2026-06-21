package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.PlanItemStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MonthlyPlanItemRequest {

    @NotNull  private Long districtId;
    @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}") private String month;
    @NotBlank private String description;
    @NotNull  private LocalDate date;
    private String responsiblePersons;
    private PlanItemStatus status = PlanItemStatus.PENDING;
}
