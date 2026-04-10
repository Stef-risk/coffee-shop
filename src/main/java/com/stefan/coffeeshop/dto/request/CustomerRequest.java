package com.stefan.coffeeshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequest {

    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 50)
    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @DecimalMin(value = "0", message = "充值金额不能为负")
    private BigDecimal topUpAmount;

    @Size(max = 200)
    private String notes;
}
