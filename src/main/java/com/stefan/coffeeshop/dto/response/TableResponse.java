package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.common.enums.TableStatus;
import com.stefan.coffeeshop.entity.DiningTable;
import lombok.Data;

@Data
public class TableResponse {
    private Long id;
    private String tableNumber;
    private Integer capacity;
    private TableStatus status;
    private String location;
    private String notes;

    public static TableResponse from(DiningTable t) {
        TableResponse r = new TableResponse();
        r.id = t.getId();
        r.tableNumber = t.getTableNumber();
        r.capacity = t.getCapacity();
        r.status = t.getStatus();
        r.location = t.getLocation();
        r.notes = t.getNotes();
        return r;
    }
}
