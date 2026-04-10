package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.entity.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findByActiveTrueOrderByDisplayOrderAscNameAsc();

    boolean existsByName(String name);
}
