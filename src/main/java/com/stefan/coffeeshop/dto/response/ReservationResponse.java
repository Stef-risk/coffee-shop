package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.entity.Reservation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long id;
    private String contactName;
    private String contactPhone;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private String tableNumber;
    private String customerName;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    public static ReservationResponse from(Reservation r) {
        ReservationResponse res = new ReservationResponse();
        res.id = r.getId();
        res.contactName = r.getContactName();
        res.contactPhone = r.getContactPhone();
        res.reservationTime = r.getReservationTime();
        res.partySize = r.getPartySize();
        res.tableNumber = r.getTable() != null ? r.getTable().getTableNumber() : null;
        res.customerName = r.getCustomer() != null ? r.getCustomer().getName() : null;
        res.status = r.getStatus();
        res.notes = r.getNotes();
        res.createdAt = r.getCreatedAt();
        return res;
    }
}
