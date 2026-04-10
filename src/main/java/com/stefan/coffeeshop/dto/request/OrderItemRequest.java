package com.stefan.coffeeshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull(message = "商品ID不能为空")
    private Long menuItemId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;

    @Size(max = 200)
    private String notes;
}
