package com.gomaa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private LocalDate date;
    private List<CartItemDTO> items;
    private double total;
    private String status; // Processing, Shipped, Delivered, Cancelled
    private String paymentStatus; // Paid, Pending, Refunded
    private String wayPayment; // CreditCard, Paypal, Cash, etc.
}
