package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.MenuItemRequest;
import com.stefan.coffeeshop.dto.response.MenuItemResponse;
import com.stefan.coffeeshop.entity.MenuCategory;
import com.stefan.coffeeshop.entity.MenuItem;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.MenuCategoryRepository;
import com.stefan.coffeeshop.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository categoryRepository;

    public PageResponse<MenuItemResponse> search(Long categoryId, String keyword,
                                                  Boolean available, Pageable pageable) {
        return PageResponse.of(
                menuItemRepository.search(categoryId, keyword, available, pageable)
                        .map(MenuItemResponse::from)
        );
    }

    public List<MenuItemResponse> listByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryIdAndAvailableTrueOrderByDisplayOrderDesc(categoryId)
                .stream().map(MenuItemResponse::from).toList();
    }

    public MenuItemResponse getById(Long id) {
        return MenuItemResponse.from(findById(id));
    }

    @Transactional
    public MenuItemResponse create(MenuItemRequest request) {
        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("菜单分类", request.getCategoryId()));

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(category)
                .imageUrl(request.getImageUrl())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse update(Long id, MenuItemRequest request) {
        MenuItem item = findById(id);
        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("菜单分类", request.getCategoryId()));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(category);
        item.setImageUrl(request.getImageUrl());
        if (request.getAvailable() != null) item.setAvailable(request.getAvailable());
        if (request.getDisplayOrder() != null) item.setDisplayOrder(request.getDisplayOrder());

        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemResponse toggleAvailability(Long id, boolean available) {
        MenuItem item = findById(id);
        item.setAvailable(available);
        return MenuItemResponse.from(menuItemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        MenuItem item = findById(id);
        menuItemRepository.delete(item);
    }

    private MenuItem findById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("菜品", id));
    }
}
