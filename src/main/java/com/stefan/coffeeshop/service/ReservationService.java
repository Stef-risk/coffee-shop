package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.ReservationRequest;
import com.stefan.coffeeshop.dto.response.ReservationResponse;
import com.stefan.coffeeshop.entity.Customer;
import com.stefan.coffeeshop.entity.DiningTable;
import com.stefan.coffeeshop.entity.Reservation;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.CustomerRepository;
import com.stefan.coffeeshop.repository.DiningTableRepository;
import com.stefan.coffeeshop.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DiningTableRepository tableRepository;
    private final CustomerRepository customerRepository;

    public PageResponse<ReservationResponse> list(String status, Pageable pageable) {
        if (status != null) {
            return PageResponse.of(
                    reservationRepository.findByStatusOrderByReservationTimeDesc(status, pageable)
                            .map(ReservationResponse::from)
            );
        }
        return PageResponse.of(
                reservationRepository.findAllByOrderByReservationTimeDesc(pageable)
                        .map(ReservationResponse::from)
        );
    }

    public List<ReservationResponse> getTodayReservations() {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return reservationRepository
                .findByReservationTimeBetweenAndStatusNotOrderByReservationTime(start, end, "CANCELLED")
                .stream().map(ReservationResponse::from).toList();
    }

    public ReservationResponse getById(Long id) {
        return ReservationResponse.from(findById(id));
    }

    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        Reservation reservation = Reservation.builder()
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .reservationTime(request.getReservationTime())
                .partySize(request.getPartySize())
                .status("PENDING")
                .notes(request.getNotes())
                .build();

        if (request.getTableId() != null) {
            DiningTable table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("餐桌", request.getTableId()));
            reservation.setTable(table);
        }

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("客户", request.getCustomerId()));
            reservation.setCustomer(customer);
        }

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, String status) {
        Reservation reservation = findById(id);
        reservation.setStatus(status);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional
    public void cancel(Long id) {
        Reservation reservation = findById(id);
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }

    private Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预订", id));
    }
}
