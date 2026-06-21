package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.AdditionalSessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdditionalSessionRequest {

    @NotNull
    private Long reportId;

    @NotNull
    private AdditionalSessionType sessionType;

    private Integer slNo;
    private LocalDate date;
    private String place;
    private String schoolName;

    @Min(0)
    private Integer participantsMale = 0;

    @Min(0)
    private Integer participantsFemale = 0;

    @Min(0)
    private Integer participantsTotal = 0;

    private String remarks;
}
