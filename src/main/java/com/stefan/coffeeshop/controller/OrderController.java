package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.common.enums.OrderStatus;
import com.stefan.coffeeshop.common.enums.OrderType;
import com.stefan.coffeeshop.dto.request.OrderRequest;
import com.stefan.coffeeshop.dto.request.PaymentRequest;
import com.stefan.coffeeshop.dto.response.OrderResponse;
import com.stefan.coffeeshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                orderService.search(status, type, startDate, endDate, pageable)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getActiveOrders() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getActiveOrders()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("订单创建成功", orderService.createOrder(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("缺少 status 字段"));
        }
        OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok("订单状态已更新", orderService.updateStatus(id, status)));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<ApiResponse<OrderResponse>> processPayment(
            @PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("支付成功", orderService.processPayment(id, request)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("订单已取消", orderService.cancelOrder(id)));
    }
}
