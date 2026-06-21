package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.TrainingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TrainingSessionRequest {

    @NotNull
    private Long reportId;

    @NotNull
    private TrainingType trainingType;

    private Integer slNo;

    private LocalDate date;

    private String place;

    private String schoolName;

    @Min(0) private Integer participantsMale   = 0;
    @Min(0) private Integer participantsFemale = 0;
    @Min(0) private Integer participantsTotal  = 0;
}
