package com.gomaa.service;

import com.gomaa.dto.PaymentTransactionRequestDTO;
import com.gomaa.dto.PaymentTransactionResponseDTO;
import com.gomaa.enums.PaymentMethod;
import com.gomaa.enums.PaymentStatus;
import com.gomaa.model.Order;
import com.gomaa.model.PaymentTransaction;
import com.gomaa.repository.OrderRepository;
import com.gomaa.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionServiceTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentTransactionServiceImpl paymentTransactionService;

    private Order order;
    private PaymentTransaction payment;

    private PaymentTransactionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);

        payment = PaymentTransaction.builder()
                .paymentId(1L)
                .order(order)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .build();

        requestDTO = new PaymentTransactionRequestDTO();
        requestDTO.setOrderId(1L);
        requestDTO.setPaymentMethod(PaymentMethod.PAYPAL);
        requestDTO.setAmount(BigDecimal.valueOf(100));
        requestDTO.setCurrency("USD");
    }

    @Test
    void createPayment_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(payment);

        PaymentTransactionResponseDTO response = paymentTransactionService.createPayment(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
        assertEquals(PaymentMethod.PAYPAL, response.getPaymentMethod());

        verify(orderRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
    }

    @Test
    void createPayment_orderNotFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentTransactionService.createPayment(requestDTO));

        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void getByOrderId_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.of(payment));

        PaymentTransactionResponseDTO response = paymentTransactionService.getByOrderId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        verify(orderRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, times(1)).findByOrder(order);
    }

    @Test
    void getByOrderId_paymentNotFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentTransactionRepository.findByOrder(order)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentTransactionService.getByOrderId(1L));

        assertEquals("Payment not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, times(1)).findByOrder(order);
    }

    @Test
    void getByOrderId_orderNotFound_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentTransactionService.getByOrderId(1L));

        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository, times(1)).findById(1L);
        verify(paymentTransactionRepository, never()).findByOrder(any());
    }
}
