package com.stefan.coffeeshop.controller;

import com.stefan.coffeeshop.common.ApiResponse;
import com.stefan.coffeeshop.dto.request.MenuCategoryRequest;
import com.stefan.coffeeshop.dto.response.MenuCategoryResponse;
import com.stefan.coffeeshop.service.MenuCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu/categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuCategoryResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> create(
            @Valid @RequestBody MenuCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("分类创建成功", categoryService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuCategoryResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MenuCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("分类已更新", categoryService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("分类已删除"));
    }
}
