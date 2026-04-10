package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
}
