package com.stefan.coffeeshop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @Size(max = 500)
    private String imageUrl;

    private Boolean available;

    private Integer displayOrder;
}
