package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.NoticePriority;
import com.breads.minds.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NoticeRequest {

    @NotBlank private String title;
    @NotBlank private String message;
    @NotNull  private NoticePriority priority;

    @NotEmpty(message = "At least one target role is required")
    private List<UserRole> targetRoles;

    private Boolean isActive = true;
    private LocalDate startDate;
    private LocalDate endDate;
}
