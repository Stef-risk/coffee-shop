package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.common.enums.TableStatus;
import com.stefan.coffeeshop.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    Optional<DiningTable> findByTableNumber(String tableNumber);

    boolean existsByTableNumber(String tableNumber);

    List<DiningTable> findByStatusOrderByTableNumber(TableStatus status);

    List<DiningTable> findAllByOrderByTableNumber();
}
