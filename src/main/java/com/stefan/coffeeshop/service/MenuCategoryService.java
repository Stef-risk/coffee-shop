package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.dto.request.MenuCategoryRequest;
import com.stefan.coffeeshop.dto.response.MenuCategoryResponse;
import com.stefan.coffeeshop.entity.MenuCategory;
import com.stefan.coffeeshop.exception.BusinessException;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.MenuCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;

    public List<MenuCategoryResponse> listAll() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc()
                .stream().map(MenuCategoryResponse::from).toList();
    }

    public MenuCategoryResponse getById(Long id) {
        return MenuCategoryResponse.from(findById(id));
    }

    @Transactional
    public MenuCategoryResponse create(MenuCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("分类名称已存在: " + request.getName());
        }
        MenuCategory category = MenuCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return MenuCategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public MenuCategoryResponse update(Long id, MenuCategoryRequest request) {
        MenuCategory category = findById(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());
        if (request.getActive() != null) category.setActive(request.getActive());
        return MenuCategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        MenuCategory category = findById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private MenuCategory findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("菜单分类", id));
    }
}
