package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.entity.MenuCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MenuCategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean active;
    private LocalDateTime createdAt;

    public static MenuCategoryResponse from(MenuCategory c) {
        MenuCategoryResponse r = new MenuCategoryResponse();
        r.id = c.getId();
        r.name = c.getName();
        r.description = c.getDescription();
        r.displayOrder = c.getDisplayOrder();
        r.active = c.getActive();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}
