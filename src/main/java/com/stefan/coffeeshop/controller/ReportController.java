package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.dto.response.SalesReportResponse;
import com.stefan.coffeeshop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<SalesReportResponse>> daily(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.ok(reportService.getDailyReport(target)));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<SalesReportResponse>> monthly(
            @RequestParam(defaultValue = "0") int year,
            @RequestParam(defaultValue = "0") int month) {
        LocalDate now = LocalDate.now();
        int y = year > 0 ? year : now.getYear();
        int m = month > 0 ? month : now.getMonthValue();
        return ResponseEntity.ok(ApiResponse.ok(reportService.getMonthlyReport(y, m)));
    }

    @GetMapping("/custom")
    public ResponseEntity<ApiResponse<SalesReportResponse>> custom(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getCustomReport(startDate, endDate)));
    }
}
