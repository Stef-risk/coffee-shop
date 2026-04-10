package com.stefan.coffeeshop.dto.request;

import com.stefan.coffeeshop.common.enums.TableStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TableRequest {

    @NotBlank(message = "桌号不能为空")
    @Size(max = 20)
    private String tableNumber;

    @NotNull(message = "容纳人数不能为空")
    @Min(value = 1, message = "容纳人数至少1人")
    private Integer capacity;

    private TableStatus status;

    @Size(max = 50)
    private String location;

    @Size(max = 200)
    private String notes;
}
