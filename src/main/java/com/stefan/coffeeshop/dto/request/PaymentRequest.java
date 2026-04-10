package com.stefan.coffeeshop.dto.request;

import com.stefan.coffeeshop.common.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "支付方式不能为空")
    private PaymentMethod paymentMethod;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal paidAmount;

    /** 微信/支付宝流水号 */
    private String transactionId;
}
