package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.dto.request.CustomerRequest;
import com.stefan.coffeeshop.dto.response.CustomerResponse;
import com.stefan.coffeeshop.entity.Customer;
import com.stefan.coffeeshop.exception.BusinessException;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public PageResponse<CustomerResponse> search(String keyword, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return PageResponse.of(
                    customerRepository.findByNameContainingIgnoreCaseOrPhoneContaining(
                            keyword, keyword, pageable).map(CustomerResponse::from)
            );
        }
        return PageResponse.of(customerRepository.findAll(pageable).map(CustomerResponse::from));
    }

    public CustomerResponse getById(Long id) {
        return CustomerResponse.from(findById(id));
    }

    public CustomerResponse getByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("客户", "手机号", phone));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (StringUtils.hasText(request.getPhone()) &&
                customerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已注册: " + request.getPhone());
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .notes(request.getNotes())
                .build();

        if (request.getTopUpAmount() != null && request.getTopUpAmount().compareTo(BigDecimal.ZERO) > 0) {
            customer.setMemberBalance(request.getTopUpAmount());
        }

        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findById(id);

        if (StringUtils.hasText(request.getPhone()) &&
                !request.getPhone().equals(customer.getPhone()) &&
                customerRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已注册: " + request.getPhone());
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setNotes(request.getNotes());

        if (request.getTopUpAmount() != null && request.getTopUpAmount().compareTo(BigDecimal.ZERO) > 0) {
            customer.setMemberBalance(customer.getMemberBalance().add(request.getTopUpAmount()));
        }

        return CustomerResponse.from(customerRepository.save(customer));
    }

    private Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("客户", id));
    }
}
