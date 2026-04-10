package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryIdAndAvailableTrueOrderByDisplayOrderDesc(Long categoryId);

    Page<MenuItem> findByAvailableTrue(Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:available IS NULL OR m.available = :available)")
    Page<MenuItem> search(@Param("categoryId") Long categoryId,
                          @Param("keyword") String keyword,
                          @Param("available") Boolean available,
                          Pageable pageable);
}
