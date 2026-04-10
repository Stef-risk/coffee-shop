package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByReservationTimeBetweenAndStatusNotOrderByReservationTime(
            LocalDateTime start, LocalDateTime end, String status);

    Page<Reservation> findByStatusOrderByReservationTimeDesc(String status, Pageable pageable);

    Page<Reservation> findAllByOrderByReservationTimeDesc(Pageable pageable);
}
