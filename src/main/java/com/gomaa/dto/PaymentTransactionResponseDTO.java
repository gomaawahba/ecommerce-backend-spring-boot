package com.gomaa.dto;

import com.gomaa.enums.PaymentMethod;
import com.gomaa.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PaymentTransactionResponseDTO {

    private Long paymentId;

    private Long orderId;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String paypalOrderId;

    private BigDecimal amount;

    private String currency;

    private Instant createdAt;
}
