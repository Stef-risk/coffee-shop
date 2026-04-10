package com.stefan.coffeeshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50)
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    @NotNull(message = "预订时间不能为空")
    @Future(message = "预订时间必须在未来")
    private LocalDateTime reservationTime;

    @NotNull(message = "用餐人数不能为空")
    @Min(value = 1, message = "用餐人数至少1人")
    private Integer partySize;

    private Long tableId;

    private Long customerId;

    @Size(max = 300)
    private String notes;
}
