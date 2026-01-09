package com.gomaa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.PaymentTransactionRequestDTO;
import com.gomaa.dto.PaymentTransactionResponseDTO;
import com.gomaa.enums.PaymentMethod;
import com.gomaa.enums.PaymentStatus;
import com.gomaa.service.PaymentTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentTransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @InjectMocks
    private PaymentTransactionController paymentTransactionController;

    private ObjectMapper objectMapper;

    private PaymentTransactionRequestDTO requestDTO;
    private PaymentTransactionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(paymentTransactionController).build();

        requestDTO = new PaymentTransactionRequestDTO();
        requestDTO.setOrderId(1L);
        requestDTO.setPaymentMethod(PaymentMethod.PAYPAL);
        requestDTO.setAmount(BigDecimal.valueOf(100));
        requestDTO.setCurrency("USD");

        responseDTO = PaymentTransactionResponseDTO.builder()
                .paymentId(1L)
                .orderId(1L)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(BigDecimal.valueOf(100))
                .currency("USD")
                .paypalOrderId("PAYPAL123")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void createPayment_success() throws Exception {
        when(paymentTransactionService.createPayment(any(PaymentTransactionRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.paymentMethod").value("PAYPAL"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));

        verify(paymentTransactionService, times(1)).createPayment(any(PaymentTransactionRequestDTO.class));
    }

    @Test
    void getPaymentByOrder_success() throws Exception {
        when(paymentTransactionService.getByOrderId(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(1))
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.paymentMethod").value("PAYPAL"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));

        verify(paymentTransactionService, times(1)).getByOrderId(1L);
    }
}

