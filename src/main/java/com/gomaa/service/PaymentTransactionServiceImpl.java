package com.gomaa.service;

import com.gomaa.dto.PaymentTransactionRequestDTO;
import com.gomaa.dto.PaymentTransactionResponseDTO;
import com.gomaa.enums.PaymentStatus;
import com.gomaa.model.Order;
import com.gomaa.model.PaymentTransaction;
import com.gomaa.repository.OrderRepository;
import com.gomaa.repository.PaymentTransactionRepository;
import com.gomaa.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentTransactionResponseDTO createPayment(PaymentTransactionRequestDTO request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentTransaction payment = PaymentTransaction.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "USD")
                .build();

        PaymentTransaction saved = paymentTransactionRepository.save(payment);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentTransactionResponseDTO getByOrderId(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentTransaction payment = paymentTransactionRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return mapToResponse(payment);
    }

    private PaymentTransactionResponseDTO mapToResponse(PaymentTransaction payment) {
        return PaymentTransactionResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paypalOrderId(payment.getPaypalOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
