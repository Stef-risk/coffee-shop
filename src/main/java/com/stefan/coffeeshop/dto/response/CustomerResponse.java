package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.common.enums.MemberLevel;
import com.stefan.coffeeshop.entity.Customer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private MemberLevel memberLevel;
    private BigDecimal totalSpent;
    private Integer visitCount;
    private BigDecimal memberBalance;
    private String notes;
    private LocalDateTime createdAt;

    public static CustomerResponse from(Customer c) {
        CustomerResponse r = new CustomerResponse();
        r.id = c.getId();
        r.name = c.getName();
        r.phone = c.getPhone();
        r.email = c.getEmail();
        r.memberLevel = c.getMemberLevel();
        r.totalSpent = c.getTotalSpent();
        r.visitCount = c.getVisitCount();
        r.memberBalance = c.getMemberBalance();
        r.notes = c.getNotes();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}
