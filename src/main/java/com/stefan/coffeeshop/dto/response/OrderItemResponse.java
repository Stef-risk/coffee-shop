package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private Long menuItemId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String notes;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse r = new OrderItemResponse();
        r.id = item.getId();
        r.menuItemId = item.getMenuItem().getId();
        r.itemName = item.getItemName();
        r.quantity = item.getQuantity();
        r.unitPrice = item.getUnitPrice();
        r.subtotal = item.getSubtotal();
        r.notes = item.getNotes();
        return r;
    }
}
