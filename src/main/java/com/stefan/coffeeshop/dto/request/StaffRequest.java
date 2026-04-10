package com.stefan.coffeeshop.dto.request;

import com.stefan.coffeeshop.common.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3-50位")
    private String username;

    /** 新建时必填，更新时可选（空则不修改密码） */
    @Size(min = 6, max = 100, message = "密码至少6位")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50)
    private String fullName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String email;

    @NotNull(message = "角色不能为空")
    private UserRole role;

    private Boolean active;
}
