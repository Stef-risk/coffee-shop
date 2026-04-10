package com.stefan.coffeeshop.service;

import com.stefan.coffeeshop.common.PageResponse;
import com.stefan.coffeeshop.common.enums.OrderStatus;
import com.stefan.coffeeshop.common.enums.OrderType;
import com.stefan.coffeeshop.common.enums.TableStatus;
import com.stefan.coffeeshop.dto.request.OrderRequest;
import com.stefan.coffeeshop.dto.request.PaymentRequest;
import com.stefan.coffeeshop.dto.response.OrderResponse;
import com.stefan.coffeeshop.entity.*;
import com.stefan.coffeeshop.exception.BusinessException;
import com.stefan.coffeeshop.exception.ResourceNotFoundException;
import com.stefan.coffeeshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final DiningTableRepository tableRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    private static final AtomicInteger SEQ = new AtomicInteger(0);

    public PageResponse<OrderResponse> search(OrderStatus status, OrderType type,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               Pageable pageable) {
        return PageResponse.of(
                orderRepository.search(status, type, startDate, endDate, pageable)
                        .map(OrderResponse::from)
        );
    }

    public OrderResponse getById(Long id) {
        return OrderResponse.from(findById(id));
    }

    public List<OrderResponse> getActiveOrders() {
        return orderRepository.findByStatusIn(
                List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING, OrderStatus.READY)
        ).stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        validateOrderRequest(request);

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .orderType(request.getOrderType())
                .status(OrderStatus.PENDING)
                .discountAmount(request.getDiscountAmount() != null ?
                        request.getDiscountAmount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        // 关联桌台
        if (request.getTableId() != null) {
            DiningTable table = tableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("餐桌", request.getTableId()));
            if (table.getStatus() == TableStatus.OCCUPIED) {
                throw new BusinessException("该桌台已有进行中的订单，请先结账或选择其他桌台");
            }
            order.setTable(table);
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        // 关联会员
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("客户", request.getCustomerId()));
            order.setCustomer(customer);
        }

        // 关联操作员工
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(order::setStaff);

        // 添加订单项
        for (var itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("菜品", itemReq.getMenuItemId()));
            if (!menuItem.getAvailable()) {
                throw new BusinessException("菜品已下架: " + menuItem.getName());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .itemName(menuItem.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(menuItem.getPrice())
                    .notes(itemReq.getNotes())
                    .build();
            orderItem.calcSubtotal();
            order.getItems().add(orderItem);
        }

        order.recalculate();
        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = findById(id);
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletedAt(LocalDateTime.now());
            // 更新客户消费统计
            if (order.getCustomer() != null) {
                Customer customer = order.getCustomer();
                customer.setTotalSpent(customer.getTotalSpent().add(order.getPayableAmount()));
                customer.setVisitCount(customer.getVisitCount() + 1);
                customerRepository.save(customer);
            }
            // 释放桌台
            if (order.getTable() != null) {
                order.getTable().setStatus(TableStatus.CLEANING);
                tableRepository.save(order.getTable());
            }
        }

        if (newStatus == OrderStatus.CANCELLED) {
            if (order.getTable() != null) {
                order.getTable().setStatus(TableStatus.AVAILABLE);
                tableRepository.save(order.getTable());
            }
        }

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse processPayment(Long id, PaymentRequest request) {
        Order order = findById(id);

        if (order.getPayment() != null) {
            throw new BusinessException("该订单已支付");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("已取消的订单无法支付");
        }
        if (request.getPaidAmount().compareTo(order.getPayableAmount()) < 0) {
            throw new BusinessException("支付金额不足，应付: " + order.getPayableAmount());
        }

        // 会员余额支付校验
        if (request.getPaymentMethod().name().equals("MEMBER")) {
            if (order.getCustomer() == null) {
                throw new BusinessException("非会员无法使用会员余额支付");
            }
            Customer customer = order.getCustomer();
            if (customer.getMemberBalance().compareTo(order.getPayableAmount()) < 0) {
                throw new BusinessException("会员余额不足");
            }
            customer.setMemberBalance(customer.getMemberBalance().subtract(order.getPayableAmount()));
            customerRepository.save(customer);
        }

        BigDecimal change = request.getPaidAmount().subtract(order.getPayableAmount());
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paidAmount(request.getPaidAmount())
                .changeAmount(change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO)
                .transactionId(request.getTransactionId())
                .build();

        order.setPayment(paymentRepository.save(payment));
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());

        if (order.getCustomer() != null) {
            Customer customer = order.getCustomer();
            customer.setTotalSpent(customer.getTotalSpent().add(order.getPayableAmount()));
            customer.setVisitCount(customer.getVisitCount() + 1);
            customerRepository.save(customer);
        }
        if (order.getTable() != null) {
            order.getTable().setStatus(TableStatus.CLEANING);
            tableRepository.save(order.getTable());
        }

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException("已完成的订单无法取消");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("订单已经是取消状态");
        }
        order.setStatus(OrderStatus.CANCELLED);
        if (order.getTable() != null) {
            order.getTable().setStatus(TableStatus.AVAILABLE);
            tableRepository.save(order.getTable());
        }
        return OrderResponse.from(orderRepository.save(order));
    }

    private Order findById(Long id) {
        return orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("订单", id));
    }

    private void validateOrderRequest(OrderRequest request) {
        if (request.getOrderType() == OrderType.DINE_IN && request.getTableId() == null) {
            throw new BusinessException("堂食订单必须选择桌台");
        }
        if (request.getOrderType() == OrderType.DELIVERY &&
                (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank())) {
            throw new BusinessException("外卖订单必须填写配送地址");
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING -> next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY -> next == OrderStatus.SERVED || next == OrderStatus.COMPLETED;
            case SERVED -> next == OrderStatus.COMPLETED;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException("订单状态不允许从 " + current + " 变更为 " + next);
        }
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = SEQ.incrementAndGet() % 10000;
        return String.format("CS-%s-%04d", date, seq);
    }
}
