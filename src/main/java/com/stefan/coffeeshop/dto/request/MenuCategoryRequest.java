package com.stefan.coffeeshop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MenuCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50)
    private String name;

    @Size(max = 200)
    private String description;

    private Integer displayOrder;

    private Boolean active;
}
