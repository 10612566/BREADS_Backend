package com.breads.minds.dto.response;

import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String name;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Long districtId;
    private String districtName;
}
