package com.breads.minds.dto.response;

import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String mobile;
    private UserRole role;
    private UserStatus status;
    private Long districtId;
    private String districtName;
    private String photoUrl;
    private LocalDateTime createdAt;
}
