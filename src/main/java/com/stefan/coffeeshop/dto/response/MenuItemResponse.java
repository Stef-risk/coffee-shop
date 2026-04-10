package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.entity.MenuItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private Boolean available;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    public static MenuItemResponse from(MenuItem item) {
        MenuItemResponse r = new MenuItemResponse();
        r.id = item.getId();
        r.name = item.getName();
        r.description = item.getDescription();
        r.price = item.getPrice();
        r.categoryId = item.getCategory().getId();
        r.categoryName = item.getCategory().getName();
        r.imageUrl = item.getImageUrl();
        r.available = item.getAvailable();
        r.displayOrder = item.getDisplayOrder();
        r.createdAt = item.getCreatedAt();
        return r;
    }
}
