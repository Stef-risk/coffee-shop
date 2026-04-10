package com.stefan.coffeeshop.dto.request;

import com.stefan.coffeeshop.common.enums.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    @NotNull(message = "订单类型不能为空")
    private OrderType orderType;

    /** 堂食时必填 */
    private Long tableId;

    /** 可选：关联会员 */
    private Long customerId;

    @NotEmpty(message = "订单至少包含一个商品")
    @Valid
    private List<OrderItemRequest> items;

    @DecimalMin(value = "0", message = "折扣金额不能为负")
    private BigDecimal discountAmount;

    @Size(max = 500)
    private String notes;

    /** 外卖配送地址 */
    @Size(max = 300)
    private String deliveryAddress;
}
