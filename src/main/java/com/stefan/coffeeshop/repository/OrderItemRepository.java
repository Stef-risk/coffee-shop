package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.itemName, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.status = 'COMPLETED' AND o.createdAt >= :start AND o.createdAt <= :end " +
           "GROUP BY oi.itemName ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopItemsByDateRange(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
}
