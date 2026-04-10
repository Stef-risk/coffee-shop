package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.common.enums.UserRole;
import com.stefan.coffeeshop.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaffResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;

    public static StaffResponse from(User user) {
        StaffResponse r = new StaffResponse();
        r.id = user.getId();
        r.username = user.getUsername();
        r.fullName = user.getFullName();
        r.phone = user.getPhone();
        r.email = user.getEmail();
        r.role = user.getRole();
        r.active = user.getActive();
        r.createdAt = user.getCreatedAt();
        return r;
    }
}
