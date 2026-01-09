package com.gomaa.dto;

import com.gomaa.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentTransactionRequestDTO {

    private Long orderId;

    private PaymentMethod paymentMethod;

    private BigDecimal amount;

    private String currency; // USD, EUR ...
}
