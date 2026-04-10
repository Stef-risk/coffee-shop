package com.stefan.coffeeshop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SalesReportResponse {

    private String period;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private BigDecimal avgOrderAmount;
    private List<TopItemResponse> topItems;
    private List<DailyRevenueResponse> dailyRevenue;

    @Data
    @Builder
    public static class TopItemResponse {
        private String itemName;
        private Long quantity;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class DailyRevenueResponse {
        private String date;
        private BigDecimal revenue;
        private Long orderCount;
    }
}
