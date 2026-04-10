package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.ReservationRequest;
import com.stefan.coffeeshop.dto.response.ReservationResponse;
import com.stefan.coffeeshop.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("reservationTime").ascending());
        return ResponseEntity.ok(ApiResponse.ok(reservationService.list(status, pageable)));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> today() {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getTodayReservations()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("预订创建成功", reservationService.create(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReservationResponse>> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("缺少 status 字段"));
        }
        return ResponseEntity.ok(ApiResponse.ok("预订状态已更新",
                reservationService.updateStatus(id, status.toUpperCase())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.ok(ApiResponse.ok("预订已取消"));
    }
}
