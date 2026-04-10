package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.common.enums.TableStatus;
import com.stefan.coffeeshop.dto.request.TableRequest;
import com.stefan.coffeeshop.dto.response.TableResponse;
import com.stefan.coffeeshop.service.TableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> list(
            @RequestParam(required = false) TableStatus status) {
        List<TableResponse> tables = status != null
                ? tableService.listByStatus(status)
                : tableService.listAll();
        return ResponseEntity.ok(ApiResponse.ok(tables));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(tableService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TableResponse>> create(@Valid @RequestBody TableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("餐桌创建成功", tableService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> update(
            @PathVariable Long id, @Valid @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("餐桌信息已更新", tableService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TableResponse>> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("缺少 status 字段"));
        }
        TableStatus status = TableStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok("桌台状态已更新", tableService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("餐桌已删除"));
    }
}
