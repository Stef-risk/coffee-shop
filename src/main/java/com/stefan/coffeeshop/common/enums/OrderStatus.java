package com.stefan.coffeeshop.common.enums;

public enum OrderStatus {
    PENDING,      // 待确认
    CONFIRMED,    // 已确认
    PREPARING,    // 制作中
    READY,        // 待取餐/待上桌
    SERVED,       // 已上桌
    COMPLETED,    // 已完成
    CANCELLED     // 已取消
}
