package com.stefan.coffeeshop.dto.response;

import com.stefan.coffeeshop.common.enums.OrderStatus;
import com.stefan.coffeeshop.common.enums.OrderType;
import com.stefan.coffeeshop.common.enums.PaymentMethod;
import com.stefan.coffeeshop.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderType orderType;
    private OrderStatus status;
    private String tableNumber;
    private String customerName;
    private String staffName;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal payableAmount;
    private String notes;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.id = order.getId();
        r.orderNumber = order.getOrderNumber();
        r.orderType = order.getOrderType();
        r.status = order.getStatus();
        r.tableNumber = order.getTable() != null ? order.getTable().getTableNumber() : null;
        r.customerName = order.getCustomer() != null ? order.getCustomer().getName() : null;
        r.staffName = order.getStaff() != null ? order.getStaff().getFullName() : null;
        r.items = order.getItems().stream().map(OrderItemResponse::from).toList();
        r.totalAmount = order.getTotalAmount();
        r.discountAmount = order.getDiscountAmount();
        r.payableAmount = order.getPayableAmount();
        r.notes = order.getNotes();
        r.deliveryAddress = order.getDeliveryAddress();
        r.paymentMethod = order.getPayment() != null ? order.getPayment().getPaymentMethod() : null;
        r.createdAt = order.getCreatedAt();
        r.completedAt = order.getCompletedAt();
        return r;
    }
}
