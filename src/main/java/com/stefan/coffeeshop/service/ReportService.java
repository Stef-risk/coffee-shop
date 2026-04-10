package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.enums.OrderStatus;
import com.stefan.coffeeshop.dto.response.SalesReportResponse;
import com.stefan.coffeeshop.repository.OrderItemRepository;
import com.stefan.coffeeshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public SalesReportResponse getDailyReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        return buildReport("日报 - " + date, start, end, false);
    }

    public SalesReportResponse getMonthlyReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return buildReport(String.format("月报 - %d年%d月", year, month), start, end, true);
    }

    public SalesReportResponse getCustomReport(LocalDateTime start, LocalDateTime end) {
        return buildReport("自定义报表", start, end, true);
    }

    private SalesReportResponse buildReport(String period, LocalDateTime start,
                                             LocalDateTime end, boolean includeDailyBreakdown) {
        BigDecimal revenue = orderRepository.sumRevenueByDateRange(start, end);
        Long totalOrders = orderRepository.countByDateRange(start, end);
        Long completedOrders = orderRepository.countByStatusAndDateRange(OrderStatus.COMPLETED, start, end);
        Long cancelledOrders = orderRepository.countByStatusAndDateRange(OrderStatus.CANCELLED, start, end);

        BigDecimal avgAmount = completedOrders > 0
                ? revenue.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<Object[]> topItemsRaw = orderItemRepository.findTopItemsByDateRange(start, end);
        List<SalesReportResponse.TopItemResponse> topItems = topItemsRaw.stream()
                .limit(10)
                .map(row -> SalesReportResponse.TopItemResponse.builder()
                        .itemName((String) row[0])
                        .quantity(((Number) row[1]).longValue())
                        .revenue((BigDecimal) row[2])
                        .build())
                .toList();

        List<SalesReportResponse.DailyRevenueResponse> dailyRevenue = new ArrayList<>();
        if (includeDailyBreakdown) {
            LocalDate current = start.toLocalDate();
            LocalDate endDate = end.toLocalDate();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            while (!current.isAfter(endDate)) {
                LocalDateTime dayStart = current.atStartOfDay();
                LocalDateTime dayEnd = current.atTime(23, 59, 59);
                BigDecimal dayRevenue = orderRepository.sumRevenueByDateRange(dayStart, dayEnd);
                Long dayOrders = orderRepository.countByStatusAndDateRange(OrderStatus.COMPLETED, dayStart, dayEnd);
                dailyRevenue.add(SalesReportResponse.DailyRevenueResponse.builder()
                        .date(current.format(fmt))
                        .revenue(dayRevenue)
                        .orderCount(dayOrders)
                        .build());
                current = current.plusDays(1);
            }
        }

        return SalesReportResponse.builder()
                .period(period)
                .totalRevenue(revenue)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .avgOrderAmount(avgAmount)
                .topItems(topItems)
                .dailyRevenue(dailyRevenue.isEmpty() ? null : dailyRevenue)
                .build();
    }
}
