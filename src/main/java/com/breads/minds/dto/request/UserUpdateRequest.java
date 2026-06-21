package com.breads.minds.dto.request;

import com.breads.minds.entity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserUpdateRequest {
    private String name;
    @Email private String email;
    private String mobile;
    private UserStatus status;
    private Long districtId;
    private String photoUrl;
}
