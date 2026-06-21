package com.breads.minds.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MindsActivityRequest {

    @NotNull  private Long districtId;
    @NotNull  private Integer year;
    @NotBlank private String childName;
    @NotNull @Min(3) @Max(20) private Integer age;
    private String className;
    @NotBlank private String schoolName;
    private String interventionType;

    @NotBlank @Pattern(regexp = "Male|Female|Other")
    private String gender;

    private String location;
    private String topicsDiscussed;
    private LocalDate session1Date;
    private LocalDate session2Date;
    private LocalDate session3Date;
    private String outcome;
    private String followUp;
    private String remarks;
}
