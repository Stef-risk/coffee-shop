package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.enums.TableStatus;
import com.stefan.coffeeshop.dto.request.TableRequest;
import com.stefan.coffeeshop.dto.response.TableResponse;
import com.stefan.coffeeshop.entity.DiningTable;
import com.stefan.coffeeshop.exception.BusinessException;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.DiningTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableService {

    private final DiningTableRepository tableRepository;

    public List<TableResponse> listAll() {
        return tableRepository.findAllByOrderByTableNumber().stream()
                .map(TableResponse::from).toList();
    }

    public List<TableResponse> listByStatus(TableStatus status) {
        return tableRepository.findByStatusOrderByTableNumber(status).stream()
                .map(TableResponse::from).toList();
    }

    public TableResponse getById(Long id) {
        return TableResponse.from(findById(id));
    }

    @Transactional
    public TableResponse create(TableRequest request) {
        if (tableRepository.existsByTableNumber(request.getTableNumber())) {
            throw new BusinessException("桌号已存在: " + request.getTableNumber());
        }
        DiningTable table = DiningTable.builder()
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .status(request.getStatus() != null ? request.getStatus() : TableStatus.AVAILABLE)
                .location(request.getLocation())
                .notes(request.getNotes())
                .build();
        return TableResponse.from(tableRepository.save(table));
    }

    @Transactional
    public TableResponse update(Long id, TableRequest request) {
        DiningTable table = findById(id);

        if (!table.getTableNumber().equals(request.getTableNumber()) &&
                tableRepository.existsByTableNumber(request.getTableNumber())) {
            throw new BusinessException("桌号已存在: " + request.getTableNumber());
        }

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        if (request.getStatus() != null) table.setStatus(request.getStatus());
        table.setLocation(request.getLocation());
        table.setNotes(request.getNotes());
        return TableResponse.from(tableRepository.save(table));
    }

    @Transactional
    public TableResponse updateStatus(Long id, TableStatus status) {
        DiningTable table = findById(id);
        table.setStatus(status);
        return TableResponse.from(tableRepository.save(table));
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        tableRepository.deleteById(id);
    }

    private DiningTable findById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("餐桌", id));
    }
}
