package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.MenuItemRequest;
import com.stefan.coffeeshop.dto.response.MenuItemResponse;
import com.stefan.coffeeshop.service.MenuItemService;
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
@RequestMapping("/menu/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MenuItemResponse>>> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("displayOrder").descending()
                .and(Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok(
                menuItemService.search(categoryId, keyword, available, pageable)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<MenuItemResponse>>> listByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.ok(menuItemService.listByCategory(categoryId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(menuItemService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemResponse>> create(
            @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("菜品创建成功", menuItemService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("菜品已更新", menuItemService.update(id, request)));
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<MenuItemResponse>> toggleAvailability(
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean available = body.get("available");
        if (available == null) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("缺少 available 字段"));
        }
        return ResponseEntity.ok(ApiResponse.ok(
                available ? "菜品已上架" : "菜品已下架",
                menuItemService.toggleAvailability(id, available)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        menuItemService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("菜品已删除"));
    }
}
